# 知识图谱智能教育 接口文档
## 请求头编码格式
POST请求统一采用默认x-www-form-urlencoded格式，UTF-8编码
```
{
  "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
}
```
## 用户认证方式
除了登录注册以外，其余接口均需要利用登录或注册获取的Token。使用方式为在Header中加入字段：
```
{
  "Token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjF9.CkZaotlKqW0J-iJwlEjiUrapjD9Fb8eNkYdQ4EHuUFU"
}
```
如果忘记加入，或者加入了错误的Token，后端会直接返回Unauthorized信息：
```
{
  "code": "401",
  "msg": "Token is invalid"
}
```
## 登录注册部分
### 用户登录接口
- 请求方式：POST
- URL：/login
#### 功能描述
用户使用注册邮箱和密码登录，以获取Token用于后续接口调用。
#### 参数
| 参数名 | 描述 | 数据类型 |
| ----- | -----| --------|
| email | 用户邮箱 | string |
| password | 用户密码 | string |
#### 成功返回范例
```
{
	"Token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjF9.CkZaotlKqW0J-iJwlEjiUrapjD9Fb8eNkYdQ4EHuUFU",
	"code": "200"
}
```
### 用户注册接口
- 请求方式：POST
- URL：/register
#### 功能描述
用户使用邮箱注册，以获取Token用于后续接口调用。若邮箱格式错误或者用户已注册则会注册失败。
#### 参数
| 参数名 | 描述 | 数据类型 |
| ----- | -----| --------|
| email | 用户邮箱 | string |
| password | 用户密码 | string |
#### 成功返回范例
```
{
	"Token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjF9.CkZaotlKqW0J-iJwlEjiUrapjD9Fb8eNkYdQ4EHuUFU",
	"code": "200"
}
```

## 数据接口部分
### 上传学科列表接口
- 请求方式：POST
- URL：/subject/upload
#### 功能描述
上传用户定制的需要显示的学科列表。
#### 参数
| 参数名 | 描述 | 数据类型 |
| ----- | -----| --------|
| subject | 用户选择的学科列表 | list |
#### 请求体范例
```
{
	"subject": ["chinese", "english"]
}
```
#### 成功返回范例
```
{
	"msg": "Success",
	"code": "200"
}
```
### 上传学科列表接口
- 请求方式：POST
- URL：/subject/download
#### 功能描述
下载用户定制的需要显示的学科列表。
#### 参数
此接口不需要任何参数。
#### 成功返回范例
```
{
	"subject": ["chinese", "english"],
	"code": "200"
}
```
