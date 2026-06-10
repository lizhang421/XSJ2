# 阿里云OSS CORS配置说明

## 问题说明

当前前端直接上传到阿里云OSS时遇到CORS跨域错误：
```
Access to XMLHttpRequest at 'https://lizhanghao.oss-cn-beijing.aliyuncs.com/...' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

## 解决方案

### 方案一：在阿里云OSS控制台配置CORS规则（推荐）

1. 登录[阿里云OSS控制台](https://oss.console.aliyun.com/)
2. 进入Bucket列表，找到 `lizhanghao` 这个Bucket
3. 点击Bucket名称进入详情页
4. 在左侧菜单找到 **数据安全** -> **跨域设置（CORS）**
5. 点击 **设置** 或 **创建规则**
6. 添加以下CORS规则：

```
来源: *
允许Methods: GET, POST, PUT, DELETE, HEAD
允许Headers: *
暴露Headers: ETag, x-oss-request-id
缓存时间: 600
```

7. 点击 **确定** 保存

### 方案二：使用后端代理上传

如果无法配置CORS，可以通过后端代理上传文件：

1. 前端将文件上传到后端API
2. 后端使用阿里云OSS SDK上传文件
3. 后端返回文件URL给前端

### 方案三：使用STS临时凭证

1. 后端生成STS临时凭证
2. 前端使用临时凭证直接上传到OSS
3. 凭证过期后需要重新获取

## 当前项目使用的配置

项目使用 **方案一**，需要在阿里云OSS控制台配置CORS规则后才能正常上传。

## OSS配置信息

```
Region: oss-cn-beijing
Bucket: lizhanghao
Endpoint: https://lizhanghao.oss-cn-beijing.aliyuncs.com
```

## 文件存储路径

- 视频文件: `video/年/月/日/时间戳_随机字符串.扩展名`
- 封面图片: `images/年/月/日/时间戳_随机字符串.扩展名`

示例：
- `video/2026/03/26/20260326162616_jpboe9.mp4`
- `images/2026/03/26/20260326162717_def456.jpg`
