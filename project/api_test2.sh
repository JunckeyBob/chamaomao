#!/bin/bash

# --- 配置变量 ---
BASE_URL="http://localhost:8080/api"
# 用户和管理员的凭证 (确保与您的 DataInitializer 或实际数据一致)
USER_PHONE_LOC_TEST="13777777777" # 为位置测试创建一个新用户，避免与其他测试冲突
USER_PASS_LOC_TEST="locationUserPass"
ADMIN_PHONE="18888888888" # 使用已有的管理员账号
ADMIN_PASS="111111"

USER_COOKIE_FILE_LOC="user_loc_cookies.txt"
ADMIN_COOKIE_FILE_LOC="admin_loc_cookies.txt"

# 测试动物的名称和ID
ANIMAL_NAME_FOR_LOC_TEST_1="定位测试犬一号"
ANIMAL_NAME_FOR_LOC_TEST_2="定位测试猫二号"
animal_id_1=""
animal_id_2=""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试结果计数器
passed_tests_loc=0
failed_tests_loc=0
total_tests_loc=0

# --- 辅助函数 (与主脚本中的类似或可以共享) ---
print_step_loc() {
    echo -e "\n${YELLOW}--- [Location Test] STEP: $1 ---${NC}"
}

print_info_loc() {
    echo -e "${GREEN}INFO: $1${NC}"
}

print_error_loc() {
    echo -e "${RED}ERROR: $1${NC}"
}

# 简化的curl检查函数 (只检查状态码和可选文本)
execute_curl_and_check_loc() {
    local method="$1"
    local url="$2"
    local cookie_file="$3"
    local data="$4"
    local expected_text="$5"
    local expected_status_code="$6"
    local response_file="response_loc.tmp"
    local headers_file="headers_loc.tmp"
    local test_description="$7" # 新增参数：测试描述

    echo "Executing for '$test_description': $method $url"
    if [ -n "$data" ]; then
        echo "Data: $data"
    fi

    local curl_cmd="curl -s -w \"\nHTTP_STATUS:%{http_code}\" -o $response_file --dump-header $headers_file"
    if [ -n "$cookie_file" ]; then curl_cmd+=" -b $cookie_file"; fi
    if [[ "$method" == "POST" || "$method" == "PUT" ]]; then
        curl_cmd+=" -H \"Content-Type: application/json\""
        if [ -n "$data" ]; then curl_cmd+=" -d '$data'"; fi
    fi
    curl_cmd+=" -X $method \"$url\""

    response_with_status=$(eval $curl_cmd)
    http_status=$(echo "$response_with_status" | grep "HTTP_STATUS:" | cut -d':' -f2)
    actual_response_body=$(cat $response_file)

    echo "--- Response Body (first 10 lines) ---"
    head -n 10 $response_file
    echo "--- End of Body Snippet ---"
    echo -e "HTTP Status: $http_status"

    local test_passed=true
    ((total_tests_loc++))

    if [ -n "$expected_status_code" ] && [ "$http_status" -ne "$expected_status_code" ]; then
        print_error_loc "[$test_description] 预期状态码 $expected_status_code, 实际为 $http_status"
        test_passed=false
    fi
    if [ -n "$expected_text" ]; then
        if echo "$actual_response_body" | grep -Fq "$expected_text"; then
            print_info_loc "[$test_description] 响应体检查通过 (包含: '$expected_text')"
        else
            print_error_loc "[$test_description] 响应体检查失败 (未包含: '$expected_text')"
            test_passed=false
        fi
    fi

    if $test_passed; then
        print_info_loc "[$test_description] 测试通过!"
        ((passed_tests_loc++))
    else
        print_error_loc "[$test_description] 测试失败!"
        ((failed_tests_loc++))
    fi
    rm -f $response_file $headers_file
    sleep 0.5 # 短暂延迟，避免时间戳过于接近
    return $(if $test_passed; then echo 0; else echo 1; fi)
}

# --- 准备阶段：登录用户和管理员，创建测试动物 ---
print_step_loc "准备工作：清理Cookie并登录"
rm -f $USER_COOKIE_FILE_LOC $ADMIN_COOKIE_FILE_LOC

# 注册并登录一个专门用于位置测试的普通用户
print_info_loc "注册位置测试用户 ($USER_PHONE_LOC_TEST)..."
execute_curl_and_check_loc "POST" "$BASE_URL/users/register" "" \
  '{"phoneNumber":"'"$USER_PHONE_LOC_TEST"'","password":"'"$USER_PASS_LOC_TEST"'","nickname":"位置测试员"}' \
  "用户注册成功" 201 || { print_error_loc "位置测试用户注册失败，中止脚本"; exit 1; }

print_info_loc "登录位置测试用户 ($USER_PHONE_LOC_TEST)..."
curl -s -X POST -H "Content-Type: application/json" \
     -d '{"phoneNumber":"'"$USER_PHONE_LOC_TEST"'","password":"'"$USER_PASS_LOC_TEST"'"}' \
     -c $USER_COOKIE_FILE_LOC "$BASE_URL/users/login" > /dev/null # 保存cookie，忽略输出
if [ ! -s $USER_COOKIE_FILE_LOC ]; then print_error_loc "$USER_COOKIE_FILE_LOC 未创建或为空"; exit 1; fi
print_info_loc "位置测试用户登录成功，Cookie已保存至 $USER_COOKIE_FILE_LOC"

print_info_loc "登录管理员 ($ADMIN_PHONE)..."
curl -s -X POST -H "Content-Type: application/json" \
     -d '{"phoneNumber":"'"$ADMIN_PHONE"'","password":"'"$ADMIN_PASS"'"}' \
     -c $ADMIN_COOKIE_FILE_LOC "$BASE_URL/users/login" > /dev/null
if [ ! -s $ADMIN_COOKIE_FILE_LOC ]; then print_error_loc "$ADMIN_COOKIE_FILE_LOC 未创建或为空"; exit 1; fi
print_info_loc "管理员登录成功，Cookie已保存至 $ADMIN_COOKIE_FILE_LOC"

print_info_loc "管理员创建测试动物1 ($ANIMAL_NAME_FOR_LOC_TEST_1)..."
animal1_data='{"name":"'"$ANIMAL_NAME_FOR_LOC_TEST_1"'","species":"犬","adoptionStatus":"AVAILABLE","healthStatus":"良好","gender":"MALE","age":"1岁"}'
response1_body=$(curl -s -X POST -H "Content-Type: application/json" -b $ADMIN_COOKIE_FILE_LOC -d "$animal1_data" "$BASE_URL/animals")
animal_id_1=$(echo "$response1_body" | jq -r '.animalId // empty')
if [ -z "$animal_id_1" ] || [ "$animal_id_1" == "null" ]; then print_error_loc "创建动物1失败"; exit 1; else print_info_loc "动物1创建成功, ID: $animal_id_1"; fi

print_info_loc "管理员创建测试动物2 ($ANIMAL_NAME_FOR_LOC_TEST_2)..."
animal2_data='{"name":"'"$ANIMAL_NAME_FOR_LOC_TEST_2"'","species":"猫","adoptionStatus":"AVAILABLE","healthStatus":"优秀","gender":"FEMALE","age":"2岁"}'
response2_body=$(curl -s -X POST -H "Content-Type: application/json" -b $ADMIN_COOKIE_FILE_LOC -d "$animal2_data" "$BASE_URL/animals")
animal_id_2=$(echo "$response2_body" | jq -r '.animalId // empty')
if [ -z "$animal_id_2" ] || [ "$animal_id_2" == "null" ]; then print_error_loc "创建动物2失败"; exit 1; else print_info_loc "动物2创建成功, ID: $animal_id_2"; fi


# --- 动物位置测试正式开始 ---

# 1. 为动物1添加3个位置 (由普通用户报告)
print_step_loc "1. 为动物ID $animal_id_1 添加3个位置记录"
location_data_1_1='{"latitude":30.0001,"longitude":120.0001}' # 假设DTO有description
location_data_1_2='{"latitude":30.0002,"longitude":120.0002}'
location_data_1_3='{"latitude":30.0003,"longitude":120.0003}'

execute_curl_and_check_loc "POST" "$BASE_URL/animals/$animal_id_1/locations" "$USER_COOKIE_FILE_LOC" "$location_data_1_1" '"latitude":30.0001' 201 "添加位置A1"
location_id_1_1=$(cat response_loc.tmp | jq -r '.locationId // empty') # 保存ID用于删除测试

execute_curl_and_check_loc "POST" "$BASE_URL/animals/$animal_id_1/locations" "$USER_COOKIE_FILE_LOC" "$location_data_1_2" '"latitude":30.0002' 201 "添加位置A2"
execute_curl_and_check_loc "POST" "$BASE_URL/animals/$animal_id_1/locations" "$USER_COOKIE_FILE_LOC" "$location_data_1_3" '"latitude":30.0003' 201 "添加位置A3 (最新)"


# 2. 为动物2添加2个位置 (由管理员报告)
print_step_loc "2. 为动物ID $animal_id_2 添加2个位置记录"
location_data_2_1='{"latitude":31.0001,"longitude":121.0001}'
location_data_2_2='{"latitude":31.0002,"longitude":121.0002}'

execute_curl_and_check_loc "POST" "$BASE_URL/animals/$animal_id_2/locations" "$ADMIN_COOKIE_FILE_LOC" "$location_data_2_1" '"latitude":31.0001' 201 "添加位置B1"
execute_curl_and_check_loc "POST" "$BASE_URL/animals/$animal_id_2/locations" "$ADMIN_COOKIE_FILE_LOC" "$location_data_2_2" '"latitude":31.0002' 201 "添加位置B2 (最新)"


# 3. 获取动物1的所有位置记录
print_step_loc "3. 获取动物ID $animal_id_1 的所有位置记录"
response_get_locs_animal1=$(curl -s -X GET -b $USER_COOKIE_FILE_LOC "$BASE_URL/animals/$animal_id_1/locations") # 允许匿名或用户查看
echo "$response_get_locs_animal1" | jq # 美化输出
# 检查是否包含3条记录，并且最新的应该是 location_data_1_3 的内容
count_locs_animal1=$(echo "$response_get_locs_animal1" | jq '. | length')
if [ "$count_locs_animal1" -eq 3 ]; then
    print_info_loc "动物ID $animal_id_1 位置记录数量正确 (3条)"
    ((passed_tests_loc++))
else
    print_error_loc "动物ID $animal_id_1 位置记录数量错误 (预期3, 实际$count_locs_animal1)"
    ((failed_tests_loc++))
fi
((total_tests_loc++))
# 检查最新一条记录的内容 (基于description，如果您的DTO有)
if echo "$response_get_locs_animal1" | jq -e '.[0] | select(.latitude==30.0003)' > /dev/null; then
    print_info_loc "动物ID $animal_id_1 最新位置记录内容初步检查通过"
else
    print_error_loc "动物ID $animal_id_1 最新位置记录内容初步检查失败"
fi


# 4. 获取最新的N条位置记录 (例如，最新的3条，应该包含上面添加的后3条)
print_step_loc "4. 获取最新的3条位置记录"
response_latest_locs=$(curl -s -X GET "$BASE_URL/locations/latest?limit=3")
echo "$response_latest_locs" | jq
count_latest_locs=$(echo "$response_latest_locs" | jq '. | length')
if [ "$count_latest_locs" -eq 3 ]; then # 假设总记录数 >= 3
    print_info_loc "获取最新3条位置记录，数量正确 (3条)"
    ((passed_tests_loc++))
    # 检查第一条是不是我们期望的最新一条 (Animal2的最新位置)
    if echo "$response_latest_locs" | jq -e '.[0] | select(.latitude==31.0002)' > /dev/null; then
        print_info_loc "最新记录内容初步检查通过 (Animal2最新位置)"
    else
        print_error_loc "最新记录内容初步检查失败"
    fi
else
    print_error_loc "获取最新3条位置记录，数量错误 (预期3, 实际$count_latest_locs)"
    ((failed_tests_loc++))
fi
((total_tests_loc++))


# 5. 管理员删除一个位置记录 (例如，动物1的第一个位置记录)
print_step_loc "5. 管理员删除位置记录 (ID: $location_id_1_1)"
if [ -n "$location_id_1_1" ] && [ "$location_id_1_1" != "null" ]; then
    execute_curl_and_check_loc "DELETE" "$BASE_URL/locations/$location_id_1_1" "$ADMIN_COOKIE_FILE_LOC" "" \
    "位置记录 ID: $location_id_1_1 已成功删除" 200 "管理员删除位置A1"
else
    print_info_loc "未获取到位置ID $location_id_1_1，跳过删除测试。"
fi

# 6. 普通用户尝试删除一个位置记录 (例如，动物2的第一个位置，假设其ID为 L2_ID)
print_step_loc "6. 普通用户尝试删除位置记录 (预期失败 403)"
location_id_b1_to_delete="" # 你需要有方法获取这个ID
response_animal2_locs=$(curl -s -X GET -b $ADMIN_COOKIE_FILE_LOC "$BASE_URL/animals/$animal_id_2/locations")
location_id_b1_to_delete=$(echo "$response_animal2_locs" | jq -r '.[1].locationId // empty') # 获取第二条（即较早的那条）

if [ -n "$location_id_b1_to_delete" ] && [ "$location_id_b1_to_delete" != "null" ]; then
    execute_curl_and_check_loc "DELETE" "$BASE_URL/locations/$location_id_b1_to_delete" "$USER_COOKIE_FILE_LOC" "" \
    "" 403 "普通用户删除位置(预期失败)"
else
    print_info_loc "未获取到用于普通用户删除权限测试的位置ID，跳过。"
fi

GUIDE_ID_1="" # 用于存储管理员创建的第一个指南ID
GUIDE_ID_2="" # 用于存储管理员创建的第二个指南ID
GUIDE_TITLE_1="安全领养宠物犬指南"
GUIDE_TITLE_2="猫咪日常护理小贴士"
GUIDE_CONTENT_1="本文详细介绍领养犬只的注意事项，包括环境准备、初期相处、健康检查等。"
GUIDE_CONTENT_2="猫咪是可爱的伙伴，本指南将提供关于猫咪饮食、清洁、娱乐和健康的基本护理建议。"
UPDATED_GUIDE_TITLE_1="[修订版] 安全领养宠物犬全攻略"

print_step_loc "7. 管理员创建第一篇领养指南 ($GUIDE_TITLE_1)"
guide1_data='{
    "title": "'"$GUIDE_TITLE_1"'",
    "content": "'"$GUIDE_CONTENT_1"'"
}'
# 使用临时文件存储响应和状态码，避免复杂的eval和管道问题
curl_response_file_guide="response_guide.tmp"
curl_status_file_guide="status_guide.tmp"

curl -s -w "%{http_code}" -o "$curl_response_file_guide" \
     -X POST -H "Content-Type: application/json" -b "$ADMIN_COOKIE_FILE_LOC" \
     -d "$guide1_data" "$BASE_URL/adoption-guides" > "$curl_status_file_guide"

guide1_status=$(cat "$curl_status_file_guide")
guide1_body=$(cat "$curl_response_file_guide")

echo "$guide1_body" | jq . # 美化输出，如果jq可用
echo -e "HTTP Status: $guide1_status"
((total_tests_loc++))
if [ "$guide1_status" -eq 201 ]; then
    GUIDE_ID_1=$(echo "$guide1_body" | jq -r '.guideId // empty')
    if [ -n "$GUIDE_ID_1" ] && [ "$GUIDE_ID_1" != "null" ]; then
        print_info_loc "指南1创建成功, ID: $GUIDE_ID_1"
        ((passed_tests_loc++))
    else
        print_error_loc "未能从响应中提取指南1的ID。"
        ((failed_tests_loc++))
    fi
else
    print_error_loc "管理员创建指南1失败 (状态: $guide1_status)"
    ((failed_tests_loc++))
fi
rm -f "$curl_response_file_guide" "$curl_status_file_guide"
sleep 0.5


print_step_loc "8. 管理员创建第二篇领养指南 ($GUIDE_TITLE_2)"
guide2_data='{
    "title": "'"$GUIDE_TITLE_2"'",
    "content": "'"$GUIDE_CONTENT_2"'"
}'
curl -s -w "%{http_code}" -o "$curl_response_file_guide" \
     -X POST -H "Content-Type: application/json" -b "$ADMIN_COOKIE_FILE_LOC" \
     -d "$guide2_data" "$BASE_URL/adoption-guides" > "$curl_status_file_guide"

guide2_status=$(cat "$curl_status_file_guide")
guide2_body=$(cat "$curl_response_file_guide")

echo "$guide2_body" | jq .
echo -e "HTTP Status: $guide2_status"
((total_tests_loc++))
if [ "$guide2_status" -eq 201 ]; then
    GUIDE_ID_2=$(echo "$guide2_body" | jq -r '.guideId // empty')
    if [ -n "$GUIDE_ID_2" ] && [ "$GUIDE_ID_2" != "null" ]; then
        print_info_loc "指南2创建成功, ID: $GUIDE_ID_2"
        ((passed_tests_loc++))
    else
        print_error_loc "未能从响应中提取指南2的ID。"
        ((failed_tests_loc++))
    fi
else
    print_error_loc "管理员创建指南2失败 (状态: $guide2_status)"
    ((failed_tests_loc++))
fi
rm -f "$curl_response_file_guide" "$curl_status_file_guide"
sleep 0.5


print_step_loc "9. (匿名/普通用户) 获取所有领养指南列表"
# execute_curl_and_check_loc 函数的参数：method, url, cookie_file, data, expected_text, expected_status_code, description
# 确保 GUIDE_ID_1 和 GUIDE_ID_2 在这里有值
if [ -n "$GUIDE_ID_1" ] && [ "$GUIDE_ID_1" != "null" ]; then
    execute_curl_and_check_loc "GET" "$BASE_URL/adoption-guides" "" "" \
    "\"guideId\":$GUIDE_ID_1" 200 "获取所有指南(检查指南1)"
else
    print_error_loc "GUIDE_ID_1 未设置，无法检查所有指南中是否包含它。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi
if [ -n "$GUIDE_ID_2" ] && [ "$GUIDE_ID_2" != "null" ]; then
    execute_curl_and_check_loc "GET" "$BASE_URL/adoption-guides" "" "" \
    "\"guideId\":$GUIDE_ID_2" 200 "获取所有指南(检查指南2)"
else
    print_error_loc "GUIDE_ID_2 未设置，无法检查所有指南中是否包含它。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


print_step_loc "10. (匿名/普通用户) 获取特定领养指南详情 (ID: $GUIDE_ID_1)"
if [ -n "$GUIDE_ID_1" ] && [ "$GUIDE_ID_1" != "null" ]; then
    execute_curl_and_check_loc "GET" "$BASE_URL/adoption-guides/$GUIDE_ID_1" "" "" \
    "\"title\":\"$GUIDE_TITLE_1\"" 200 "获取指南1详情"
else
    print_info_loc "未获取到指南1的ID，跳过获取详情测试。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


print_step_loc "11. 管理员更新领养指南 (ID: $GUIDE_ID_1)"
if [ -n "$GUIDE_ID_1" ] && [ "$GUIDE_ID_1" != "null" ]; then
    update_guide1_data='{
        "title": "'"$UPDATED_GUIDE_TITLE_1"'",
        "content": "'"$GUIDE_CONTENT_1 内容已更新。"'"
    }'
    execute_curl_and_check_loc "PUT" "$BASE_URL/adoption-guides/$GUIDE_ID_1" "$ADMIN_COOKIE_FILE_LOC" \
    "$update_guide1_data" "$UPDATED_GUIDE_TITLE_1" 200 "更新指南1"
else
    print_info_loc "未获取到指南1的ID，跳过更新测试。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


print_step_loc "12. 普通用户尝试更新领养指南 (ID: $GUIDE_ID_2) (预期失败 403)"
if [ -n "$GUIDE_ID_2" ] && [ "$GUIDE_ID_2" != "null" ]; then
    dummy_update_data='{"title":"普通用户尝试修改","content":"此修改不应成功"}'
    execute_curl_and_check_loc "PUT" "$BASE_URL/adoption-guides/$GUIDE_ID_2" "$USER_COOKIE_FILE_LOC" \
    "$dummy_update_data" "" 403 "普通用户更新指南(预期失败)"
else
    print_info_loc "未获取到指南2的ID，跳过普通用户更新测试。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


print_step_loc "13. 管理员删除领养指南 (ID: $GUIDE_ID_1)"
if [ -n "$GUIDE_ID_1" ] && [ "$GUIDE_ID_1" != "null" ]; then
    execute_curl_and_check_loc "DELETE" "$BASE_URL/adoption-guides/$GUIDE_ID_1" "$ADMIN_COOKIE_FILE_LOC" "" \
    "领养指南 ID: $GUIDE_ID_1 已成功删除" 200 "管理员删除指南1"
    if [ $? -eq 0 ]; then # 检查 execute_curl_and_check_loc 的返回码
        # 验证指南1是否真的被删除了
        execute_curl_and_check_loc "GET" "$BASE_URL/adoption-guides/$GUIDE_ID_1" "" "" \
        "" 404 "验证指南1已删除(预期404)"
    fi
else
    print_info_loc "未获取到指南1的ID，跳过删除测试。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


print_step_loc "14. 普通用户尝试删除领养指南 (ID: $GUIDE_ID_2) (预期失败 403)"
if [ -n "$GUIDE_ID_2" ] && [ "$GUIDE_ID_2" != "null" ]; then
    execute_curl_and_check_loc "DELETE" "$BASE_URL/adoption-guides/$GUIDE_ID_2" "$USER_COOKIE_FILE_LOC" "" \
    "" 403 "普通用户删除指南(预期失败)"
else
    print_info_loc "未获取到指南2的ID，跳过普通用户删除测试。"
    ((total_tests_loc++)); ((failed_tests_loc++))
fi


# --- 脚本原有测试结束部分 ---
print_step_loc "动物领养指南及之前模块 API 测试脚本执行完毕"
echo -e "\n${YELLOW}=========================================${NC}"
echo -e "${YELLOW}      总体测试结果统计 (位置和指南)       ${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo -e "${GREEN}通过的测试个数: $passed_tests_loc${NC}"
if [ "$failed_tests_loc" -gt 0 ]; then
    echo -e "${RED}失败的测试个数: $failed_tests_loc${NC}"
else
    echo -e "${GREEN}失败的测试个数: $failed_tests_loc${NC}"
fi
echo -e "总共执行测试数 (本脚本): $total_tests_loc"
echo -e "${YELLOW}=========================================${NC}"
echo "请检查各个步骤的输出，特别是HTTP状态码和响应体检查结果。"
echo "Cookie文件保存在: $USER_COOKIE_FILE_LOC 和 $ADMIN_COOKIE_FILE_LOC (如果登录成功)"
echo "若再次测试，请先重启后端。"

# rm -f $USER_COOKIE_FILE_LOC $ADMIN_COOKIE_FILE_LOC
