#!/bin/bash

# --- 配置变量 ---
BASE_URL="http://localhost:8080/api"
USER_PHONE="13700002222"
USER_PASS_ORIGINAL="114514"
USER_PASS_NEW="123456"
ADMIN_PHONE="18888888888"
ADMIN_PASS="111111" # 确保这是您DataInitializer中设置的管理员密码

USER_COOKIE_FILE="user_cookies.txt"
ADMIN_COOKIE_FILE="admin_cookies.txt"

# 颜色定义 (可选，使输出更易读)
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

passed_tests=0
failed_tests=0
total_tests=0

# --- 辅助函数 ---
print_step() {
    echo -e "\n${YELLOW}-----------------------------------------------------${NC}"
    echo -e "${YELLOW}STEP: $1${NC}"
    echo -e "${YELLOW}-----------------------------------------------------${NC}"
}

print_info() {
    echo -e "${GREEN}INFO: $1${NC}"
}

print_error() {
    echo -e "${RED}ERROR: $1${NC}"
}

# 函数：执行curl并检查响应
# 参数1: HTTP方法 (POST, PUT, GET, DELETE)
# 参数2: URL
# 参数3: Cookie文件名 (可选, 传入 "" 则不使用cookie)
# 参数4: 请求体数据 (可选, JSON字符串，传入 "" 则无请求体)
# 参数5: 预期响应体中包含的文本 (用于grep检查)
# 参数6: 预期HTTP状态码 (可选，数字)
execute_curl_and_check() {
    local method="$1"
    local url="$2"
    local cookie_file="$3"
    local data="$4"
    local expected_text="$5"
    local expected_status_code="$6"
    local response_file="response.tmp"
    local headers_file="headers.tmp"

    echo "Executing: $method $url"
    if [ -n "$data" ]; then
        echo "Data: $data"
    fi

    local curl_cmd="curl -s -w \"%{http_code}\" -o $response_file --dump-header $headers_file" # -s 静默, -w 输出http_code, -o 保存响应体, --dump-header 保存响应头

    if [ -n "$cookie_file" ]; then
        if [[ "$method" == "POST" && "$url" == *"/login"* ]]; then # 登录时保存cookie
            curl_cmd+=" -c $cookie_file"
        else # 其他请求使用cookie
            curl_cmd+=" -b $cookie_file"
        fi
    fi

    if [[ "$method" == "POST" || "$method" == "PUT" ]]; then
        curl_cmd+=" -H \"Content-Type: application/json\""
        if [ -n "$data" ]; then
            curl_cmd+=" -d '$data'"
        fi
    fi

    curl_cmd+=" -X $method \"$url\""

    # 执行命令并获取HTTP状态码
    http_status=$(eval $curl_cmd)

    echo "--- Response Headers (headers.tmp) ---"
    cat $headers_file
    echo "--- Response Body (response.tmp) ---"
    cat $response_file
    echo -e "\nHTTP Status: $http_status"

    local test_passed=true

    if [ -n "$expected_status_code" ] && [ "$http_status" -ne "$expected_status_code" ]; then
        print_error "预期状态码 $expected_status_code, 实际为 $http_status"
        test_passed=false
    fi

    if [ -n "$expected_text" ]; then
        if grep -q "$expected_text" $response_file; then
            print_info "响应体检查通过 (包含: '$expected_text')"
        else
            print_error "响应体检查失败 (未包含: '$expected_text')"
            test_passed=false
        fi
    fi

 if $test_passed; then
        print_info "测试通过!"
        ((passed_tests++)) # 增加通过测试的计数
    else
        print_error "测试失败!"
        ((failed_tests++)) # 增加失败测试的计数
    fi
    ((total_tests++)) # 增加总测试数的计数
    echo "----------------------------------------"
    rm -f $response_file $headers_file # 清理临时文件
    sleep 1 # 短暂延迟
    return $(if $test_passed; then echo 0; else echo 1; fi) # 返回测试结果
}

# --- 清理和准备 ---
print_step "清理旧的Cookie文件"
rm -f $USER_COOKIE_FILE $ADMIN_COOKIE_FILE

# --- 测试开始 ---
print_step "启动 API 测试脚本"

# 1. 用户注册
print_step "1. 用户注册 (手机号: $USER_PHONE)"
USER_REG_DATA='{
    "phoneNumber": "'"$USER_PHONE"'",
    "password": "'"$USER_PASS_ORIGINAL"'",
    "nickname": "脚本测试用户",
    "address": "自动化测试市",
    "petExperience": "精通脚本养宠"
}'
execute_curl_and_check "POST" "$BASE_URL/users/register" "" "$USER_REG_DATA" "用户注册成功！" 201
if [ $? -ne 0 ]; then print_error "用户注册失败，中止后续测试"; exit 1; fi

# 1.1 用户注册 - 尝试注册已存在的手机号 (预期失败)
print_step "1.1 用户注册 - 手机号已存在 (预期失败)"
execute_curl_and_check "POST" "$BASE_URL/users/register" "" "$USER_REG_DATA" "该手机号已被注册" 400 # 假设错误信息包含这个

# 2. 用户登录
print_step "2. 用户登录 (手机号: $USER_PHONE, 密码: $USER_PASS_ORIGINAL)"
USER_LOGIN_DATA='{"phoneNumber": "'"$USER_PHONE"'", "password": "'"$USER_PASS_ORIGINAL"'"}'
execute_curl_and_check "POST" "$BASE_URL/users/login" "$USER_COOKIE_FILE" "$USER_LOGIN_DATA" "登录成功！" 200
if [ $? -ne 0 ]; then print_error "用户登录失败，中止后续依赖Cookie的测试"; exit 1; fi
if [ ! -s $USER_COOKIE_FILE ]; then
    print_error "$USER_COOKIE_FILE 未创建或为空，后续用户操作将失败。"
    exit 1
fi

# 3. 管理员登录
print_step "3. 管理员登录 (手机号: $ADMIN_PHONE)"
ADMIN_LOGIN_DATA='{"phoneNumber": "'"$ADMIN_PHONE"'", "password": "'"$ADMIN_PASS"'"}'
execute_curl_and_check "POST" "$BASE_URL/users/login" "$ADMIN_COOKIE_FILE" "$ADMIN_LOGIN_DATA" "登录成功！" 200
if [ $? -ne 0 ]; then print_error "管理员登录失败，中止后续依赖管理员Cookie的测试"; exit 1; fi
if [ ! -s $ADMIN_COOKIE_FILE ]; then
    print_error "$ADMIN_COOKIE_FILE 未创建或为空，后续管理员操作将失败。"
    exit 1
fi

# 4. 用户信息修改
print_step "4. 用户信息修改 (使用用户Cookie)"
USER_PROFILE_UPDATE_DATA='{
    "address": "自动化测试市高级区",
    "petExperience": "专家级脚本养宠大师"
}'
execute_curl_and_check "PUT" "$BASE_URL/users/profile" "$USER_COOKIE_FILE" "$USER_PROFILE_UPDATE_DATA" "用户信息更新成功！" 200

# 5. 用户密码修改
print_step "5. 用户密码修改 (使用用户Cookie, 原始密码: $USER_PASS_ORIGINAL, 新密码: $USER_PASS_NEW)"
USER_PASS_UPDATE_DATA='{
    "currentPassword": "'"$USER_PASS_ORIGINAL"'",
    "newPassword": "'"$USER_PASS_NEW"'",
    "confirmNewPassword": "'"$USER_PASS_NEW"'"
}'
execute_curl_and_check "PUT" "$BASE_URL/users/password" "$USER_COOKIE_FILE" "$USER_PASS_UPDATE_DATA" "密码更新成功！" 200
if [ $? -ne 0 ]; then print_error "用户密码修改失败，后续新密码登录测试可能不准确"; fi

# 5.1 用户使用新密码登录
print_step "5.1 用户使用新密码登录 (手机号: $USER_PHONE, 新密码: $USER_PASS_NEW)"
rm -f $USER_COOKIE_FILE # 删除旧的用户cookie
USER_LOGIN_NEW_PASS_DATA='{"phoneNumber": "'"$USER_PHONE"'", "password": "'"$USER_PASS_NEW"'"}'
execute_curl_and_check "POST" "$BASE_URL/users/login" "$USER_COOKIE_FILE" "$USER_LOGIN_NEW_PASS_DATA" "登录成功！" 200
if [ $? -ne 0 ]; then print_info "如果上一步密码修改成功，这里应该登录成功。"; fi
if [ ! -s $USER_COOKIE_FILE ]; then print_error "$USER_COOKIE_FILE (新密码登录后) 未创建或为空。"; fi

# --- 动物相关测试 ---
ADMIN_ANIMAL_NAME="脚本猫管理员版"
USER_ANIMAL_NAME="脚本狗用户版"
ADMIN_CREATED_ANIMAL_ID="" # 用于存储管理员创建的动物ID
USER_CREATED_ANIMAL_ID=""  # 用于存储用户创建的动物ID

# 6. 动物发布（管理员）
print_step "6. 动物发布 (管理员)"
ADMIN_ANIMAL_DATA='{
    "name": "'"$ADMIN_ANIMAL_NAME"'", "species": "猫", "breed": "脚本猫种", "age": "3月", "gender": "MALE",
    "healthStatus": "管理员添加-健康", "adoptionStatus": "AVAILABLE",
    "photoUrls": ["http://example.com/admin_cat.jpg"],
    "initialLocation": {"latitude": 30.0, "longitude": 120.0, "description": "管理员添加"}
}'
admin_animal_response_body=$(curl -s -X POST -H "Content-Type: application/json" -b $ADMIN_COOKIE_FILE -d "$ADMIN_ANIMAL_DATA" "$BASE_URL/animals")
echo "$admin_animal_response_body" # 打印完整响应体
if echo "$admin_animal_response_body" | grep -q '"animalId"'; then
    ADMIN_CREATED_ANIMAL_ID=$(echo "$admin_animal_response_body" | jq -r '.animalId // empty') # jq -r '.animalId // empty' 避免null导致错误
    if [ -n "$ADMIN_CREATED_ANIMAL_ID" ] && [ "$ADMIN_CREATED_ANIMAL_ID" != "null" ]; then
        print_info "管理员发布的动物ID: $ADMIN_CREATED_ANIMAL_ID"
        if echo "$admin_animal_response_body" | grep -q '"adoptionStatus":"AVAILABLE"'; then
            print_info "状态检查: 管理员发布的动物状态为 AVAILABLE (通过)"
            ((passed_tests++))
        else
            print_error "状态检查: 管理员发布的动物状态不是 AVAILABLE (失败)"
            ((failed_tests++))
        fi
    else
        print_error "未能从响应中提取管理员创建的动物ID。"
        ADMIN_CREATED_ANIMAL_ID="" # 确保为空
        ((failed_tests++))
    fi
else
    print_error "管理员创建动物失败或响应格式不符。"
    ((failed_tests++))
fi


# 7. 动物发布（普通用户）
print_step "7. 动物发布 (普通用户)"
USER_ANIMAL_DATA='{
    "name": "'"$USER_ANIMAL_NAME"'", "species": "狗", "breed": "脚本犬种", "age": "4月", "gender": "FEMALE",
    "healthStatus": "用户提交-待检查",
    "photoUrls": ["http://example.com/user_dog.jpg"],
    "initialLocation": {"latitude": 31.0, "longitude": 121.0, "description": "用户提交"}
}'
user_animal_response_body=$(curl -s -X POST -H "Content-Type: application/json" -b $USER_COOKIE_FILE -d "$USER_ANIMAL_DATA" "$BASE_URL/animals")
echo "$user_animal_response_body" # 打印完整响应体
if echo "$user_animal_response_body" | grep -q '"animalId"'; then
    USER_CREATED_ANIMAL_ID=$(echo "$user_animal_response_body" | jq -r '.animalId // empty')
    if [ -n "$USER_CREATED_ANIMAL_ID" ] && [ "$USER_CREATED_ANIMAL_ID" != "null" ]; then
        print_info "普通用户发布的动物ID: $USER_CREATED_ANIMAL_ID"
        if echo "$user_animal_response_body" | grep -q '"adoptionStatus":"INFO_PENDING_REVIEW"'; then # 确保这是您Service中设置的状态
            print_info "状态检查: 用户发布的动物状态为 INFO_PENDING_REVIEW (通过)"
            ((passed_tests++))
        else
            print_error "状态检查: 用户发布的动物状态不是 INFO_PENDING_REVIEW (失败)"
            ((failed_tests++))
        fi
    else
        print_error "未能从响应中提取用户创建的动物ID。"
        USER_CREATED_ANIMAL_ID="" # 确保为空
        ((failed_tests++))
    fi
else
    print_error "普通用户创建动物失败或响应格式不符。"
    ((failed_tests++))
fi


# 8. 动物审核 (管理员审核普通用户提交的动物)
print_step "8. 动物审核 (管理员审核动物ID: $USER_CREATED_ANIMAL_ID)"
if [ -z "$USER_CREATED_ANIMAL_ID" ] || [ "$USER_CREATED_ANIMAL_ID" == "null" ]; then
    print_info "警告: 未能获取用户提交的动物ID，跳过审核测试。"
else
    # 审核通过
    execute_curl_and_check "PUT" "$BASE_URL/animals/$USER_CREATED_ANIMAL_ID/review?approved=true" "$ADMIN_COOKIE_FILE" "" '"adoptionStatus":"AVAILABLE"' 200
fi

# 9. 管理员获取所有动物列表 (再次检查)
print_step "9. 管理员获取所有动物列表 (包含已审核的)"
execute_curl_and_check "GET" "$BASE_URL/animals" "$ADMIN_COOKIE_FILE" "" "" 200 # 简单检查是否成功获取

# 10. 管理员删除动物 (例如，删除管理员自己创建的那个)
if [ -n "$ADMIN_CREATED_ANIMAL_ID" ] && [ "$ADMIN_CREATED_ANIMAL_ID" != "null" ]; then
    print_step "10. 管理员删除动物 (ID: $ADMIN_CREATED_ANIMAL_ID)"
    execute_curl_and_check "DELETE" "$BASE_URL/animals/$ADMIN_CREATED_ANIMAL_ID" "$ADMIN_COOKIE_FILE" "" "动物信息删除成功" 200
else
    print_info "未获取到管理员创建的动物ID，跳过删除测试。"
fi

# 11. 普通用户尝试删除动物 (预期失败)
if [ -n "$USER_CREATED_ANIMAL_ID" ] && [ "$USER_CREATED_ANIMAL_ID" != "null" ]; then
    print_step "11. 普通用户尝试删除动物 (ID: $USER_CREATED_ANIMAL_ID) (预期403 Forbidden)"
    execute_curl_and_check "DELETE" "$BASE_URL/animals/$USER_CREATED_ANIMAL_ID" "$USER_COOKIE_FILE" "" "" 403 # 预期403，不检查响应体
else
    print_info "未获取到用户创建的动物ID，跳过普通用户删除测试。"
fi

# --- 照片相关测试变量 ---
PHOTO_FILE_PATH="/home/user1639/chamaomao/project/src/example.jpg"  # 准备一个测试用的图片文件 # TODO: change it!
PHOTO_ID="2"  # 用于存储上传的照片ID

# 12. 上传动物照片 (用户上传)
print_step "12. 上传动物照片 (用户上传到动物ID: $USER_CREATED_ANIMAL_ID)"
if [ -z "$USER_CREATED_ANIMAL_ID" ] || [ "$USER_CREATED_ANIMAL_ID" == "null" ]; then
    print_info "警告: 未获取到用户创建的动物ID，跳过照片上传测试。"
else
    if [ -f "$PHOTO_FILE_PATH" ]; then
        # 使用curl上传文件
        upload_response=$(curl -s -w "\n%{http_code}" -b $USER_COOKIE_FILE \
            -F "file=@$PHOTO_FILE_PATH" \
            "$BASE_URL/animals/$USER_CREATED_ANIMAL_ID/photos")
        
        # 分离响应体和状态码
        upload_http_status=$(echo "$upload_response" | tail -n1)
        upload_response_body=$(echo "$upload_response" | head -n -1)
        
        echo "--- 上传照片响应 ---"
        echo "$upload_response_body"
        echo "HTTP Status: $upload_http_status"
        
        if [ "$upload_http_status" -eq 201 ]; then
            print_info "照片上传成功!"
            PHOTO_ID=$(echo "$upload_response_body" | jq -r '.photoId // empty')
            if [ -n "$PHOTO_ID" ] && [ "$PHOTO_ID" != "null" ]; then
                print_info "上传的照片ID: $PHOTO_ID"
            else
                print_error "未能从响应中提取照片ID。"
                PHOTO_ID=""
            fi
        else
            print_error "照片上传失败!"
        fi
    else
        print_error "测试图片文件 $PHOTO_FILE_PATH 不存在，跳过上传测试。"
    fi
fi

# 13. 获取动物照片列表
print_step "13. 获取动物照片列表 (动物ID: $USER_CREATED_ANIMAL_ID)"
if [ -z "$USER_CREATED_ANIMAL_ID" ] || [ "$USER_CREATED_ANIMAL_ID" == "null" ]; then
    print_info "警告: 未获取到动物ID，跳过获取照片列表测试。"
else
    execute_curl_and_check "GET" "$BASE_URL/animals/$USER_CREATED_ANIMAL_ID/photos" "" "" '"photoId":' 200
fi

# 14. 删除动物照片
print_step "14. 删除动物照片 (照片ID: $PHOTO_ID)"
if [ -z "$PHOTO_ID" ] || [ "$PHOTO_ID" == "null" ]; then
    print_info "警告: 未获取到照片ID，跳过删除测试。"
else
    execute_curl_and_check "DELETE" "$BASE_URL/animals/$USER_CREATED_ANIMAL_ID/photos/$PHOTO_ID" "$USER_COOKIE_FILE" "" "图片删除成功" 200
fi

# --- 领养申请与审批测试 ---
USER_APPLICATION_ID="" # 用于存储普通用户提交的领养申请ID
TARGET_ANIMAL_ID_FOR_ADOPTION="" # 初始化为空

print_step "准备领养申请测试的目标动物"

# 尝试使用之前管理员创建的动物 ADMIN_CREATED_ANIMAL_ID
if [ -n "$ADMIN_CREATED_ANIMAL_ID" ] && [ "$ADMIN_CREATED_ANIMAL_ID" != "null" ]; then
    print_info "检查之前管理员创建的动物 (ID: $ADMIN_CREATED_ANIMAL_ID) 是否仍然可用..."
    animal_check_response=$(curl -s -w "\n%{http_code}" -X GET -b $ADMIN_COOKIE_FILE "$BASE_URL/animals/$ADMIN_CREATED_ANIMAL_ID")
    animal_check_status=$(echo "$animal_check_response" | tail -n1)
    animal_check_body=$(echo "$animal_check_response" | head -n -1)

    if [ "$animal_check_status" -eq 200 ]; then
        if echo "$animal_check_body" | grep -q '"adoptionStatus":"AVAILABLE"'; then
            TARGET_ANIMAL_ID_FOR_ADOPTION="$ADMIN_CREATED_ANIMAL_ID"
            print_info "动物 ID: $TARGET_ANIMAL_ID_FOR_ADOPTION 存在且状态为 AVAILABLE，将用于领养测试。"
        else
            print_info "动物 ID: $ADMIN_CREATED_ANIMAL_ID 存在但状态不是 AVAILABLE ($(echo "$animal_check_body" | jq -r .adoptionStatus // "未知"))。将尝试创建新动物。"
        fi
    else
        print_info "动物 ID: $ADMIN_CREATED_ANIMAL_ID 未找到或获取失败 (状态: $animal_check_status)。将尝试创建新动物。"
    fi
fi

# 如果 TARGET_ANIMAL_ID_FOR_ADOPTION 仍然为空，则管理员创建一个新的测试动物
if [ -z "$TARGET_ANIMAL_ID_FOR_ADOPTION" ]; then
    print_info "管理员重新创建一个用于领养申请测试的动物..."
    NEW_TEST_ANIMAL_NAME="领养测试专用猫"
    NEW_TEST_ANIMAL_DATA='{
        "name": "'"$NEW_TEST_ANIMAL_NAME"'", "species": "猫", "breed": "测试专用", "age": "6月", "gender": "FEMALE",
        "healthStatus": "健康，等待领养", "adoptionStatus": "AVAILABLE",
        "photoUrls": ["http://example.com/test_adopt_cat.jpg"],
        "initialLocation": {"latitude": 32.0, "longitude": 118.0, "description": "测试数据"}
    }'
    new_test_animal_response_body=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -b $ADMIN_COOKIE_FILE -d "$NEW_TEST_ANIMAL_DATA" "$BASE_URL/animals")
    new_test_animal_status=$(echo "$new_test_animal_response_body" | tail -n1)
    new_test_animal_body=$(echo "$new_test_animal_response_body" | head -n -1)

    echo "$new_test_animal_body" # 打印完整响应体
    if [ "$new_test_animal_status" -eq 201 ]; then
        if echo "$new_test_animal_body" | grep -q '"animalId"'; then
            TARGET_ANIMAL_ID_FOR_ADOPTION=$(echo "$new_test_animal_body" | jq -r '.animalId // empty')
            if [ -n "$TARGET_ANIMAL_ID_FOR_ADOPTION" ] && [ "$TARGET_ANIMAL_ID_FOR_ADOPTION" != "null" ]; then
                print_info "新创建的领养测试动物ID: $TARGET_ANIMAL_ID_FOR_ADOPTION"
            else
                print_error "未能从响应中提取新创建的领养测试动物ID。"
                TARGET_ANIMAL_ID_FOR_ADOPTION=""
            fi
        else
            print_error "新创建领养测试动物的响应格式不符。"
            TARGET_ANIMAL_ID_FOR_ADOPTION=""
        fi
    else
        print_error "管理员创建新的领养测试动物失败 (状态: $new_test_animal_status)。"
        TARGET_ANIMAL_ID_FOR_ADOPTION=""
    fi
fi

# 确保我们有一个有效的 TARGET_ANIMAL_ID_FOR_ADOPTION
if [ -z "$TARGET_ANIMAL_ID_FOR_ADOPTION" ] || [ "$TARGET_ANIMAL_ID_FOR_ADOPTION" == "null" ]; then
    print_error "严重错误：未能准备好用于领养申请测试的动物ID。中止领养申请相关测试。"
    exit 1
else
    print_info "将使用动物ID: $TARGET_ANIMAL_ID_FOR_ADOPTION 进行后续的领养申请测试。"
fi


print_step "15. 普通用户提交领养申请 (动物ID: $TARGET_ANIMAL_ID_FOR_ADOPTION)"
if [ -n "$TARGET_ANIMAL_ID_FOR_ADOPTION" ] && [ "$TARGET_ANIMAL_ID_FOR_ADOPTION" != "null" ]; then
    USER_APPLICATION_DATA='{
        "animalId": '$TARGET_ANIMAL_ID_FOR_ADOPTION',
        "details": "我非常有爱心，有丰富的养宠经验，家庭环境适合养这只可爱的动物。"
    }'
    application_response_body=$(curl -s -X POST -H "Content-Type: application/json" -b $USER_COOKIE_FILE -d "$USER_APPLICATION_DATA" "$BASE_URL/adoption-applications")
    echo "$application_response_body" # 打印完整响应体
    if echo "$application_response_body" | grep -q '"adoptionId"'; then
        USER_APPLICATION_ID=$(echo "$application_response_body" | jq -r '.adoptionId // empty')
        if [ -n "$USER_APPLICATION_ID" ] && [ "$USER_APPLICATION_ID" != "null" ]; then
            print_info "领养申请提交成功，申请ID: $USER_APPLICATION_ID"
            if echo "$application_response_body" | grep -q '"status":"PENDING_REVIEW"'; then
                print_info "状态检查: 新提交的领养申请状态为 PENDING_REVIEW (通过)"
            else
                print_error "状态检查: 新提交的领养申请状态不是 PENDING_REVIEW (失败)"
            fi
            # 检查动物状态是否变为 PENDING_ADOPTION
            animal_after_apply_response=$(curl -s -X GET -b $USER_COOKIE_FILE "$BASE_URL/animals/$TARGET_ANIMAL_ID_FOR_ADOPTION")
            if echo "$animal_after_apply_response" | grep -q '"adoptionStatus":"PENDING_ADOPTION"'; then
                print_info "动物状态检查: 动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 状态变为 PENDING_ADOPTION (通过)"
                ((passed_tests++))
            else
                print_error "动物状态检查: 动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 状态未变为 PENDING_ADOPTION (失败)"
                ((failed_tests++))
                echo "动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 当前信息: $animal_after_apply_response"
            fi
        else
            print_error "未能从响应中提取领养申请ID。"
            ((failed_tests++))
            USER_APPLICATION_ID=""
        fi
    else
        print_error "提交领养申请失败或响应格式不符。"
        ((failed_tests++))
        echo "响应: $application_response_body"
    fi
else
    print_info "警告: 无有效动物ID，跳过提交领养申请测试。"
fi


print_step "16. 管理员查看所有待审核领养申请"
# 预期应该能看到上面用户提交的申请
execute_curl_and_check "GET" "$BASE_URL/adoption-applications/admin/all?status=PENDING_REVIEW" "$ADMIN_COOKIE_FILE" "" "\"adoptionId\":$USER_APPLICATION_ID" 200


print_step "17. 管理员审核领养申请 - 批准 (申请ID: $USER_APPLICATION_ID)"
if [ -z "$USER_APPLICATION_ID" ] || [ "$USER_APPLICATION_ID" == "null" ]; then
    print_info "警告: 未能获取领养申请ID，跳过批准测试。"
else
    ADMIN_REVIEW_APPROVE_DATA='{
        "status": "APPROVED",
        "reviewDetails": "申请人条件符合，批准领养。"
    }'
    execute_curl_and_check "PUT" "$BASE_URL/adoption-applications/$USER_APPLICATION_ID/review" "$ADMIN_COOKIE_FILE" "$ADMIN_REVIEW_APPROVE_DATA" '"status":"APPROVED"' 200
    if [ $? -eq 0 ]; then
        # 验证动物状态是否变为 ADOPTED
        animal_after_approval_response=$(curl -s -X GET -b $ADMIN_COOKIE_FILE "$BASE_URL/animals/$TARGET_ANIMAL_ID_FOR_ADOPTION")
        if echo "$animal_after_approval_response" | grep -q '"adoptionStatus":"ADOPTED"'; then
            print_info "动物状态检查: 动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 状态成功变为 ADOPTED (通过)"
            ((passed_tests++))
        else
            print_error "动物状态检查: 动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 状态未变为 ADOPTED (失败)"
            echo "动物 $TARGET_ANIMAL_ID_FOR_ADOPTION 当前信息: $animal_after_approval_response"
            ((failed_tests++))
        fi
    fi
fi

ANIMAL_ID_FOR_REJECT_CANCEL=""
print_step "准备用于拒绝/取消测试的第二个目标动物"
# 这里简化处理，假设我们再创建一个。实践中，您可能希望从一个池中获取或确保其存在。
SECOND_TEST_ANIMAL_NAME="领养拒绝测试猫"
SECOND_TEST_ANIMAL_DATA='{
    "name": "'"$SECOND_TEST_ANIMAL_NAME"'", "species": "猫", "breed": "拒绝专用", "age": "5月", "gender": "MALE",
    "healthStatus": "健康", "adoptionStatus": "AVAILABLE",
    "photoUrls": ["http://example.com/test_reject_cat.jpg"],
    "initialLocation": {"latitude": 33.0, "longitude": 119.0, "description": "测试数据2"}
}'
second_test_animal_response_body=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -b $ADMIN_COOKIE_FILE -d "$SECOND_TEST_ANIMAL_DATA" "$BASE_URL/animals")
second_test_animal_status=$(echo "$second_test_animal_response_body" | tail -n1)
second_test_animal_body=$(echo "$second_test_animal_response_body" | head -n -1)
echo "$second_test_animal_body"
if [ "$second_test_animal_status" -eq 201 ]; then
    if echo "$second_test_animal_body" | grep -q '"animalId"'; then
        ANIMAL_ID_FOR_REJECT_CANCEL=$(echo "$second_test_animal_body" | jq -r '.animalId // empty')
        if [ -n "$ANIMAL_ID_FOR_REJECT_CANCEL" ] && [ "$ANIMAL_ID_FOR_REJECT_CANCEL" != "null" ]; then
            print_info "新创建的用于拒绝/取消测试的动物ID: $ANIMAL_ID_FOR_REJECT_CANCEL"
        else
            ANIMAL_ID_FOR_REJECT_CANCEL="" # 确保为空
        fi
    fi
fi
if [ -z "$ANIMAL_ID_FOR_REJECT_CANCEL" ] || [ "$ANIMAL_ID_FOR_REJECT_CANCEL" == "null" ]; then
    print_error "严重错误：未能准备好用于拒绝/取消测试的第二个动物ID。"
    exit 1
fi


print_step "18. 普通用户提交第二个领养申请 (用于测试拒绝，动物ID: $ANIMAL_ID_FOR_REJECT_CANCEL)"
if [ -n "$ANIMAL_ID_FOR_REJECT_CANCEL" ]; then
    USER_SECOND_APP_DATA='{
        "animalId": '$ANIMAL_ID_FOR_REJECT_CANCEL',
        "details": "这是我的第二个领养申请，希望这次能成功。"
    }'
    second_app_response_body=$(curl -s -X POST -H "Content-Type: application/json" -b $USER_COOKIE_FILE -d "$USER_SECOND_APP_DATA" "$BASE_URL/adoption-applications")
    echo "$second_app_response_body"
    if echo "$second_app_response_body" | grep -q '"adoptionId"'; then
        SECOND_USER_APPLICATION_ID=$(echo "$second_app_response_body" | jq -r '.adoptionId // empty')
        if [ -n "$SECOND_USER_APPLICATION_ID" ] && [ "$SECOND_USER_APPLICATION_ID" != "null" ]; then
            print_info "第二个领养申请提交成功，申请ID: $SECOND_USER_APPLICATION_ID"
        else
            print_error "未能从响应中提取第二个领养申请ID。"
            SECOND_USER_APPLICATION_ID=""
        fi
    else
        print_error "提交第二个领养申请失败或响应格式不符。"
    fi
else
    print_info "警告: 无有效动物ID ($ANIMAL_ID_FOR_REJECT_CANCEL)，跳过提交第二个领养申请测试。"
fi


print_step "19. 管理员审核领养申请 - 拒绝 (申请ID: $SECOND_USER_APPLICATION_ID)"
if [ -z "$SECOND_USER_APPLICATION_ID" ] || [ "$SECOND_USER_APPLICATION_ID" == "null" ]; then
    print_info "警告: 未能获取第二个领养申请ID，跳过拒绝测试。"
else
    ADMIN_REVIEW_REJECT_DATA='{
        "status": "REJECTED",
        "reviewDetails": "抱歉，您的当前条件暂不满足领养要求。"
    }'
    execute_curl_and_check "PUT" "$BASE_URL/adoption-applications/$SECOND_USER_APPLICATION_ID/review" "$ADMIN_COOKIE_FILE" "$ADMIN_REVIEW_REJECT_DATA" '"status":"REJECTED"' 200
    if [ $? -eq 0 ]; then
        # 验证动物状态是否恢复为 AVAILABLE (如果之前是 PENDING_ADOPTION)
        animal_after_rejection_response=$(curl -s -X GET -b $ADMIN_COOKIE_FILE "$BASE_URL/animals/$ANIMAL_ID_FOR_REJECT_CANCEL")
        if echo "$animal_after_rejection_response" | grep -q '"adoptionStatus":"AVAILABLE"'; then # 假设会恢复
            print_info "动物状态检查: 动物 $ANIMAL_ID_FOR_REJECT_CANCEL 状态成功恢复为 AVAILABLE (通过)"
        else
            print_error "动物状态检查: 动物 $ANIMAL_ID_FOR_REJECT_CANCEL 状态未恢复为 AVAILABLE (失败)"
            echo "动物 $ANIMAL_ID_FOR_REJECT_CANCEL 当前信息: $animal_after_rejection_response"
        fi
    fi
fi

# --- 准备第三个申请用于测试取消 ---
# 假设 ID=2 的动物现在是 AVAILABLE (上一步拒绝后恢复了)
THIRD_USER_APPLICATION_ID=""
print_step "20. 普通用户提交第三个领养申请 (用于测试取消，动物ID: $ANIMAL_ID_FOR_REJECT_CANCEL)"
if [ -n "$ANIMAL_ID_FOR_REJECT_CANCEL" ]; then
    # 确保动物是AVAILABLE
    animal_check_response=$(curl -s -X GET -b $USER_COOKIE_FILE "$BASE_URL/animals/$ANIMAL_ID_FOR_REJECT_CANCEL")
    if ! echo "$animal_check_response" | grep -q '"adoptionStatus":"AVAILABLE"'; then
        print_error "动物 $ANIMAL_ID_FOR_REJECT_CANCEL 当前不是 AVAILABLE 状态，无法进行取消测试的前置申请。当前状态: $(echo "$animal_check_response" | jq -r .adoptionStatus)"
    else
        USER_THIRD_APP_DATA='{ "animalId": '$ANIMAL_ID_FOR_REJECT_CANCEL', "details": "我想申请这个，但可能稍后会取消。"}'
        third_app_response_body=$(curl -s -X POST -H "Content-Type: application/json" -b $USER_COOKIE_FILE -d "$USER_THIRD_APP_DATA" "$BASE_URL/adoption-applications")
        echo "$third_app_response_body"
        if echo "$third_app_response_body" | grep -q '"adoptionId"'; then
            THIRD_USER_APPLICATION_ID=$(echo "$third_app_response_body" | jq -r '.adoptionId // empty')
            if [ -n "$THIRD_USER_APPLICATION_ID" ] && [ "$THIRD_USER_APPLICATION_ID" != "null" ]; then
                print_info "第三个领养申请提交成功，申请ID: $THIRD_USER_APPLICATION_ID"
            else
                print_error "未能从响应中提取第三个领养申请ID。"
                THIRD_USER_APPLICATION_ID=""
            fi
        else
            print_error "提交第三个领养申请失败或响应格式不符。"
        fi
    fi
else
    print_info "警告: 无有效动物ID ($ANIMAL_ID_FOR_REJECT_CANCEL)，跳过提交第三个领养申请测试。"
fi


print_step "21. 普通用户取消自己的领养申请 (申请ID: $THIRD_USER_APPLICATION_ID)"
if [ -z "$THIRD_USER_APPLICATION_ID" ] || [ "$THIRD_USER_APPLICATION_ID" == "null" ]; then
    print_info "警告: 未能获取第三个领养申请ID，跳过取消测试。"
else
    execute_curl_and_check "PUT" "$BASE_URL/adoption-applications/$THIRD_USER_APPLICATION_ID/cancel" "$USER_COOKIE_FILE" "" '"status":"CANCELLED"' 200
    if [ $? -eq 0 ]; then
        # 验证动物状态是否恢复为 AVAILABLE (如果之前是 PENDING_ADOPTION)
        animal_after_cancel_response=$(curl -s -X GET -b $USER_COOKIE_FILE "$BASE_URL/animals/$ANIMAL_ID_FOR_REJECT_CANCEL")
        if echo "$animal_after_cancel_response" | grep -q '"adoptionStatus":"AVAILABLE"'; then # 假设会恢复
            print_info "动物状态检查: 动物 $ANIMAL_ID_FOR_REJECT_CANCEL 状态成功恢复为 AVAILABLE (通过)"
        else
            print_error "动物状态检查: 动物 $ANIMAL_ID_FOR_REJECT_CANCEL 状态未恢复为 AVAILABLE (失败)"
            echo "动物 $ANIMAL_ID_FOR_REJECT_CANCEL 当前信息: $animal_after_cancel_response"
        fi
    fi
fi

print_step "22. 普通用户查看自己的领养申请列表"
execute_curl_and_check "GET" "$BASE_URL/adoption-applications/my-applications" "$USER_COOKIE_FILE" "" "" 200 # 简单检查成功，具体内容会比较多

print_step "23. 管理员查看所有领养申请 (不过滤状态)"
execute_curl_and_check "GET" "$BASE_URL/adoption-applications/admin/all" "$ADMIN_COOKIE_FILE" "" "" 200

# --- 测试结束 ---
print_step "API 测试脚本执行完毕"
# ... print_step "API 测试脚本执行完毕" ...

echo -e "\n${YELLOW}=========================================${NC}"
echo -e "${YELLOW}           测试结果统计            ${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo -e "${GREEN}通过的测试个数: $passed_tests${NC}"
if [ "$failed_tests" -gt 0 ]; then
    echo -e "${RED}失败的测试个数: $failed_tests${NC}"
else
    echo -e "${GREEN}失败的测试个数: $failed_tests${NC}"
fi
((total_tests = passed_tests + failed_tests))
echo -e "总共执行测试数: $total_tests"
echo -e "${YELLOW}=========================================${NC}"

echo "请检查各个步骤的输出，特别是HTTP状态码和响应体检查结果。"
echo "Cookie文件保存在: $USER_COOKIE_FILE 和 $ADMIN_COOKIE_FILE (如果登录成功)"
echo "若再次测试，请先重启后端。"