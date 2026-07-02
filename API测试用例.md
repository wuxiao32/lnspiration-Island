# 灵感岛后端 API 测试用例

> 基础地址：`http://localhost:8080`  
> 认证方式：请求头 `token: <JWT_TOKEN>`  
> 日期：2026-07-02

---

## 目录

1. [环境准备](#环境准备)
2. [用户模块 `/user`](#1-用户模块-user)
3. [灵感模块 `/inspiration`](#2-灵感模块-inspiration)
4. [评论模块 `/comment`](#3-评论模块-comment)
5. [点赞模块 `/like`](#4-点赞模块-like)
6. [收藏模块 `/collect`](#5-收藏模块-collect)
7. [完整业务流程测试](#6-完整业务流程测试)
8. [异常场景汇总](#7-异常场景汇总)

---

## 环境准备

### 启动服务

```bash
# 终端1：启动后端
cd inspiration-island
mvn spring-boot:run

# 终端2：测试（用 curl 或 Postman）
```

### 测试变量

```bash
# 设置基础 URL（Windows Git Bash / Linux / Mac）
export BASE="http://localhost:8080"

# 测试用账号
export USER_A="testuser1"
export PASS_A="123456"
export USER_B="testuser2"
export PASS_B="654321"
```

### 状态码速查

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 业务错误（用户名已存在、密码错误等） |
| 401 | 未登录或 token 过期 |
| 403 | 无权限（操作他人资源） |
| 404 | 资源不存在 |
| 500 | 服务器异常 |

---

## 1. 用户模块 `/user`

### 1.1 注册 `POST /user/register`

**无需登录**

#### 用例 1.1.1：正常注册

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| URL | `/user/register` |
| Content-Type | `application/json` |

```json
{
  "username": "testuser1",
  "password": "123456"
}
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "注册成功"
}
```

**curl：**
```bash
curl -X POST $BASE/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"123456"}'
```

#### 用例 1.1.2：重复用户名注册

```json
{
  "username": "testuser1",
  "password": "111111"
}
```

**预期响应：**
```json
{
  "code": 400,
  "msg": "用户名已存在",
  "data": null
}
```

#### 用例 1.1.3：注册第二个用户（用于后续跨用户测试）

```bash
curl -X POST $BASE/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"654321"}'
```

---

### 1.2 登录 `POST /user/login`

**无需登录**

#### 用例 1.2.1：正常登录

```json
{
  "username": "testuser1",
  "password": "123456"
}
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEiLCJ1c2VySWQiOjEsImlhdCI6MTc1OTk5OTk5OSwiZXhwIjoxNzYwMDAzNTk5fQ.xxx"
}
```

**curl：**
```bash
curl -X POST $BASE/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"123456"}'
```

> 📌 将返回的 token 保存为变量：`export TOKEN_A="<返回的token>"`

#### 用例 1.2.2：用户名不存在

```json
{
  "username": "nobody",
  "password": "123456"
}
```

**预期响应：**
```json
{
  "code": 400,
  "msg": "用户名不存在",
  "data": null
}
```

#### 用例 1.2.3：密码错误

```json
{
  "username": "testuser1",
  "password": "wrongpassword"
}
```

**预期响应：**
```json
{
  "code": 400,
  "msg": "密码错误",
  "data": null
}
```

---

### 1.3 用户主页 `GET /user/profile/{id}`

**无需登录**

#### 用例 1.3.1：查看存在用户的主页

| 项目 | 内容 |
|------|------|
| 方法 | `GET` |
| URL | `/user/profile/1` |

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser1",
    "inspirationCount": 0,
    "commentCount": 0
  }
}
```

**curl：**
```bash
curl -X GET $BASE/user/profile/1
```

#### 用例 1.3.2：查看不存在的用户

```bash
curl -X GET $BASE/user/profile/9999
```

**预期响应：**
```json
{
  "code": 404,
  "msg": "用户不存在",
  "data": null
}
```

---

## 2. 灵感模块 `/inspiration`

### 2.1 灵感广场 `GET /inspiration/list`

**无需登录**

```bash
curl -X GET $BASE/inspiration/list
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "xxx",
      "content": "xxx",
      "userId": 1,
      "username": "testuser1",
      "createTime": "2026-07-02 16:00:00",
      "commentList": null
    }
  ]
}
```

> ⚠️ 初始数据为空时返回 `[]`

---

### 2.2 发布灵感 `POST /inspiration/publish`

**需要登录（token 请求头）**

#### 用例 2.2.1：用 testuser1 发布灵感

```bash
curl -X POST $BASE/inspiration/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{
    "title": "我的第一条灵感",
    "content": "今天天气真好，适合出去走走，呼吸新鲜空气。"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "发布成功"
}
```

#### 用例 2.2.2：发布更多灵感（用于列表测试）

```bash
# 第二条
curl -X POST $BASE/inspiration/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{"title":"读书笔记：人类简史","content":"尤瓦尔·赫拉利提出了认知革命的概念，人类之所以能够大规模合作，是因为我们相信共同的虚构故事。"}'

# 第三条
curl -X POST $BASE/inspiration/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{"title":"编程感悟","content":"Spring Boot 的三层架构让代码更加清晰，Controller 负责请求响应，Service 负责业务逻辑，Mapper 负责数据访问。"}'
```

#### 用例 2.2.3：未登录发布

```bash
curl -X POST $BASE/inspiration/publish \
  -H "Content-Type: application/json" \
  -d '{"title":"test","content":"test content"}'
```

**预期响应：**
```json
{
  "code": 401,
  "msg": "未登录，请先登录"
}
```

#### 用例 2.2.4：用 testuser2 发布灵感

```bash
# 先登录获取 TOKEN_B
export TOKEN_B=$(curl -s -X POST $BASE/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"654321"}' | grep -o '"data":"[^"]*"' | cut -d'"' -f4)

# 发布
curl -X POST $BASE/inspiration/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{"title":"testuser2 的灵感","content":"这是第二个用户的灵感内容。"}'
```

---

### 2.3 修改灵感 `PUT /inspiration/update`

**需要登录（仅作者）**

#### 用例 2.3.1：作者修改自己的灵感

```bash
curl -X PUT $BASE/inspiration/update \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{
    "id": 1,
    "title": "我的第一条灵感（已修改）",
    "content": "修改后的内容：今天天气真好！"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "修改成功"
}
```

#### 用例 2.3.2：修改不存在的灵感

```bash
curl -X PUT $BASE/inspiration/update \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{"id":9999,"title":"xxx","content":"xxx"}'
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "灵感不存在",
  "data": null
}
```

#### 用例 2.3.3：非作者修改他人的灵感（testuser2 改 testuser1 的）

```bash
curl -X PUT $BASE/inspiration/update \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{"id":1,"title":"被黑了","content":"xxx"}'
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "只能修改自己的灵感",
  "data": null
}
```

---

### 2.4 删除灵感 `DELETE /inspiration/delete/{id}`

**需要登录（仅作者）**

#### 用例 2.4.1：非作者删除他人的灵感

```bash
curl -X DELETE $BASE/inspiration/delete/1 \
  -H "token: $TOKEN_B"
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "只能删除自己的灵感",
  "data": null
}
```

#### 用例 2.4.2：作者删除自己的灵感

```bash
curl -X DELETE $BASE/inspiration/delete/1 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "删除成功"
}
```

> 删除后再次调用 `GET /inspiration/list`，该灵感不再出现（软删除，is_delete=1）

#### 用例 2.4.3：再次删除已删除的灵感

```bash
curl -X DELETE $BASE/inspiration/delete/1 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "灵感不存在",
  "data": null
}
```

---

### 2.5 我的灵感 `GET /inspiration/myList`

**需要登录**

#### 用例 2.5.1：查看 testuser1 的灵感

```bash
curl -X GET $BASE/inspiration/myList \
  -H "token: $TOKEN_A"
```

**预期响应：** 只返回 userId=1 的灵感列表（不包含已删除的）

#### 用例 2.5.2：未登录查看

```bash
curl -X GET $BASE/inspiration/myList
```

**预期响应：** `code: 401`

---

## 3. 评论模块 `/comment`

### 3.1 发布评论 `POST /comment/publish`

**需要登录**

#### 用例 3.1.1：正常发布评论

```bash
curl -X POST $BASE/comment/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{
    "inspirationId": 2,
    "content": "写得真不错！"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "评论成功"
}
```

#### 用例 3.1.2：testuser2 也来评论

```bash
curl -X POST $BASE/comment/publish \
  -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{
    "inspirationId": 2,
    "content": "赞同，很有启发！"
  }'
```

#### 用例 3.1.3：未登录评论

```bash
curl -X POST $BASE/comment/publish \
  -H "Content-Type: application/json" \
  -d '{"inspirationId":2,"content":"test"}'
```

**预期响应：** `code: 401`

---

### 3.2 评论列表 `GET /comment/list/{inspirationId}`

**无需登录**

```bash
curl -X GET $BASE/comment/list/2
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "inspirationId": 2,
      "userId": 1,
      "username": "testuser1",
      "content": "写得真不错！",
      "createTime": "2026-07-02 16:05:00"
    },
    {
      "id": 2,
      "inspirationId": 2,
      "userId": 2,
      "username": "testuser2",
      "content": "赞同，很有启发！",
      "createTime": "2026-07-02 16:06:00"
    }
  ]
}
```

---

### 3.3 删除评论 `DELETE /comment/delete/{id}`

**需要登录（仅作者）**

#### 用例 3.3.1：非作者删除他人的评论

```bash
# testuser2 尝试删除 testuser1 的评论（id=1）
curl -X DELETE $BASE/comment/delete/1 \
  -H "token: $TOKEN_B"
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "只能删除自己的评论",
  "data": null
}
```

#### 用例 3.3.2：作者删除自己的评论

```bash
# testuser1 删除自己的评论（id=1）
curl -X DELETE $BASE/comment/delete/1 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "删除成功"
}
```

#### 用例 3.3.3：删除不存在的评论

```bash
curl -X DELETE $BASE/comment/delete/9999 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 403,
  "msg": "评论不存在",
  "data": null
}
```

---

## 4. 点赞模块 `/like`

### 4.1 点赞/取消点赞 `POST /like/toggle/{inspirationId}`

**需要登录**

#### 用例 4.1.1：点赞

```bash
curl -X POST $BASE/like/toggle/2 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "点赞成功"
}
```

#### 用例 4.1.2：取消点赞

```bash
curl -X POST $BASE/like/toggle/2 \
  -H "token: $TOKEN_A"
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "取消点赞成功"
}
```

#### 用例 4.1.3：再次点赞（重新点赞）

```bash
curl -X POST $BASE/like/toggle/2 \
  -H "token: $TOKEN_A"
```

**预期响应：** `"点赞成功"`（因为上次取消了，这次重新新建一条记录）

#### 用例 4.1.4：未登录点赞

```bash
curl -X POST $BASE/like/toggle/2
```

**预期响应：** `code: 401`

---

### 4.2 点赞数量 `GET /like/count/{inspirationId}`

**无需登录**

```bash
curl -X GET $BASE/like/count/2
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

#### 用例 4.2.1：无人点赞的灵感

```bash
curl -X GET $BASE/like/count/9999
```

**预期响应：** `"data": 0`

---

## 5. 收藏模块 `/collect`

### 5.1 收藏/取消收藏 `POST /collect/toggle/{inspirationId}`

**需要登录**

#### 用例 5.1.1：收藏

```bash
curl -X POST $BASE/collect/toggle/2 \
  -H "token: $TOKEN_B"
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "收藏成功"
}
```

#### 用例 5.1.2：取消收藏

```bash
curl -X POST $BASE/collect/toggle/2 \
  -H "token: $TOKEN_B"
```

**预期响应：** `"取消收藏成功"`

#### 用例 5.1.3：未登录收藏

```bash
curl -X POST $BASE/collect/toggle/2
```

**预期响应：** `code: 401`

---

### 5.2 收藏数量 `GET /collect/count/{inspirationId}`

**无需登录**

```bash
curl -X GET $BASE/collect/count/2
```

---

## 6. 完整业务流程测试

### 场景：两个用户完整交互

```bash
#!/bin/bash
BASE="http://localhost:8080"

echo "========== 1. 注册两个用户 =========="
curl -s -X POST $BASE/user/register -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"111111"}' | jq
curl -s -X POST $BASE/user/register -H "Content-Type: application/json" \
  -d '{"username":"bob","password":"222222"}' | jq

echo "========== 2. 登录获取 Token =========="
TOKEN_A=$(curl -s -X POST $BASE/user/login -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"111111"}' | jq -r '.data')
TOKEN_B=$(curl -s -X POST $BASE/user/login -H "Content-Type: application/json" \
  -d '{"username":"bob","password":"222222"}' | jq -r '.data')
echo "Alice Token: ${TOKEN_A:0:30}..."
echo "Bob Token: ${TOKEN_B:0:30}..."

echo "========== 3. Alice 发布灵感 =========="
curl -s -X POST $BASE/inspiration/publish -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{"title":"Alice 的灵感","content":"分享一个有趣的想法..."}' | jq

echo "========== 4. Bob 发布灵感 =========="
curl -s -X POST $BASE/inspiration/publish -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{"title":"Bob 的灵感","content":"今天学到了新知识！"}' | jq

echo "========== 5. 查看灵感广场 =========="
curl -s -X GET $BASE/inspiration/list | jq '.data | length'

echo "========== 6. Bob 给 Alice 的灵感点赞 =========="
curl -s -X POST $BASE/like/toggle/1 -H "token: $TOKEN_B" | jq

echo "========== 7. Bob 收藏 Alice 的灵感 =========="
curl -s -X POST $BASE/collect/toggle/1 -H "token: $TOKEN_B" | jq

echo "========== 8. Bob 评论 Alice 的灵感 =========="
curl -s -X POST $BASE/comment/publish -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{"inspirationId":1,"content":"很棒的想法！"}' | jq

echo "========== 9. 查看 Alice 灵感的评论 =========="
curl -s -X GET $BASE/comment/list/1 | jq '.data'

echo "========== 10. Alice 查看自己的灵感列表 =========="
curl -s -X GET $BASE/inspiration/myList -H "token: $TOKEN_A" | jq '.data | length'

echo "========== 11. Alice 修改自己的灵感 =========="
curl -s -X PUT $BASE/inspiration/update -H "Content-Type: application/json" \
  -H "token: $TOKEN_A" \
  -d '{"id":1,"title":"Alice 的灵感（修订版）","content":"修改后的内容"}' | jq

echo "========== 12. Bob 尝试修改 Alice 的灵感（应失败） =========="
curl -s -X PUT $BASE/inspiration/update -H "Content-Type: application/json" \
  -H "token: $TOKEN_B" \
  -d '{"id":1,"title":"HACKED","content":"xxx"}' | jq

echo "========== 13. 查看用户主页 =========="
curl -s -X GET $BASE/user/profile/1 | jq '.data'

echo "========== 14. Alice 删除自己的灵感 =========="
curl -s -X DELETE $BASE/inspiration/delete/1 -H "token: $TOKEN_A" | jq

echo "========== 15. 验证已删除 =========="
curl -s -X GET $BASE/inspiration/list | jq '.data | length'

echo ""
echo "========== 全部测试完成 =========="
```

---

## 7. 异常场景汇总

| # | 场景 | 请求 | 预期 code | 预期 msg |
|---|------|------|-----------|----------|
| 1 | 未登录访问需登录接口 | 无 token | 401 | 未登录，请先登录 |
| 2 | Token 过期 | 过期 token | 401 | token已过期 |
| 3 | Token 伪造 | 随机字符串 | 401 | token非法 |
| 4 | 重复注册 | POST /user/register | 400 | 用户名已存在 |
| 5 | 登录用户名不存在 | POST /user/login | 400 | 用户名不存在 |
| 6 | 登录密码错误 | POST /user/login | 400 | 密码错误 |
| 7 | 修改不存在的灵感 | PUT /inspiration/update | 403 | 灵感不存在 |
| 8 | 修改他人灵感 | PUT /inspiration/update | 403 | 只能修改自己的灵感 |
| 9 | 删除他人灵感 | DELETE /inspiration/delete/{id} | 403 | 只能删除自己的灵感 |
| 10 | 删除不存在的灵感 | DELETE /inspiration/delete/{id} | 403 | 灵感不存在 |
| 11 | 删除他人评论 | DELETE /comment/delete/{id} | 403 | 只能删除自己的评论 |
| 12 | 查看不存在用户 | GET /user/profile/{id} | 404 | 用户不存在 |

---

### 一键测试脚本

将上面的"完整业务流程测试"脚本保存为 `test_api.sh`：

```bash
# 运行（需要安装 jq 来格式化 JSON，没有的话去掉 | jq 即可）
chmod +x test_api.sh
./test_api.sh
```

如果没有 `jq`，用以下方式安装：
- **Windows (Git Bash)**：`winget install jqlang.jq`
- **Mac**：`brew install jq`
- **Linux**：`apt install jq` 或 `yum install jq`
