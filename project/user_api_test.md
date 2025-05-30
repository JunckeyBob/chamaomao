- 测试时可以自行尝试密码错误等情况。

### 用户注册
curl -i -X POST \
-H "Content-Type: application/json" \
-d '{
"phoneNumber": "13700002222",
"password": "114514",
"nickname": "陶卓",
"address": "debian市",
"petExperience": "熟悉命令行养宠"
}' \
http://localhost:8080/api/users/register

### 用户登录
curl -i -X POST \
-H "Content-Type: application/json" \
-d '{
"phoneNumber": "13700002222",
"password": "114514"
}' \
-c cookies.txt \
http://localhost:8080/api/users/login

### 用户信息修改
curl -i -X PUT \
-H "Content-Type: application/json" \
-d '{
"address": "debian市bin区cat路",
"petExperience": "多年命令行养宠经验"
}' \
-b cookies.txt \
http://localhost:8080/api/users/profile

### 用户密码修改
curl -i -X PUT \
-H "Content-Type: application/json" \
-d '{
"currentPassword": "114514",
"newPassword": "123456",
"confirmNewPassword": "123456",
}' \
-b cookies.txt \
http://localhost:8080/api/users/password