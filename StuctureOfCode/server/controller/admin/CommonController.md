
```
package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}

```
很好 👍 这是 **苍穹外卖里的通用文件上传接口（上传到阿里云 OSS）**
我给你来一次 **逐行详细解释版**，和上次 CategoryController 一样风格，直接就能当笔记用。

---

# 🧾 CommonController 逐行详解 —— 阿里云 OSS 文件上传

---

## 🧱 1. 包名与 import

```java
package com.sky.controller.admin;
```

* 说明该类在后台管理端 Controller 包下
* 和 `/admin` 路径一一对应

```java
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
```

作用：

* `MessageConstant`：存放统一提示消息常量（如“上传失败”）
* `Result`：统一返回结果封装
* `AliOssUtil`：对 OSS 上传操作的工具类（核心上传逻辑在这里）

```java
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
```

👉 Swagger 接口文档注解
用于生成接口说明页面

```java
import lombok.extern.slf4j.Slf4j;
```

👉 给类自动生成 `log` 日志对象，无需手写 Logger

```java
import org.springframework.web.multipart.MultipartFile;
```

👉 Spring MVC 上传文件的类型

* 前端表单 / axios 上传文件时对应这个对象

```java
import java.io.IOException;
import java.util.UUID;
```

* `IOException`：处理文件读写时可能抛出的异常
* `UUID`：生成全球唯一字符串，用作新文件名

---

## 🧱 2. 类定义与注解

```java
/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
```

逐行解释：

* 注释：说明该类作用是 “通用接口”
* `@RestController`

  * 标识这是 REST 风格接口
  * 方法返回 JSON，而不是页面
* `@RequestMapping("/admin/common")`

  * 所有接口都以这个为前缀
  * 最终路径：`/admin/common/upload`
* `@Api(tags = "通用接口")`

  * Swagger 中接口分组名
* `@Slf4j`

  * 生成 `log.info / log.error` 日志对象

---

## 🧱 3. 注入 OSS 工具类

```java
    @Autowired
    private AliOssUtil aliOssUtil;
```

说明：

* `AliOssUtil` 是封装的工具类
* 里面封装：

```
endpoint
keyId
secret
bucketName
上传逻辑
权限策略
```

Controller **只负责调用，不关心细节**
👉 符合单一职责原则

---

## 🧩 4. 上传接口方法本体

```java
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
```

逐行解释：

* `@PostMapping("/upload")`

  * HTTP 方法：POST
  * URL 路径：

    ```
    /admin/common/upload
    ```
* `@ApiOperation("文件上传")`

  * Swagger 中接口名称
* `public Result<String> upload(MultipartFile file)`

  * 前端传入 `file`
  * Spring 自动封装为 `MultipartFile`
* `log.info("文件上传：{}",file);`

  * 记录日志，便于排错

🚨 注意：

前端必须 form-data 传参，key= **file**

---

## 🧠 5. try-catch 捕获上传异常

```java
        try {
```

说明：

* 下面代码**可能抛出 IOException**
* 所以用 try-catch 捕获
* 防止程序直接 500 崩溃

---

## 🔎 6. 获取原始文件名

```java
            //原始文件名
            String originalFilename = file.getOriginalFilename();
```

例子：

| 用户上传        | originalFilename |
| ----------- | ---------------- |
| avatar.png  | avatar.png       |
| pic.jpg     | pic.jpg          |
| abc.def.png | abc.def.png      |

---

## ✂️ 7. 提取文件后缀名

```java
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
```

逐步解释：

* `lastIndexOf(".")`

  * 找最后一个 `.` 的位置
* `substring(...)`

  * 截取到最后
* 结果示例：

| 文件名      | extension |
| -------- | --------- |
| a.png    | .png      |
| b.jpg    | .jpg      |
| c.tar.gz | .gz       |

---

## 🆕 8. 生成唯一文件名

```java
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;
```

说明：

* UUID：随机唯一字符串
* 避免**文件名覆盖**
* 例子：

| 原名    | 新名                                       |
| ----- | ---------------------------------------- |
| a.png | 7e98c7d2-ab9c-11e9-a2a3-2a2ae2dbcce4.png |

👉 解决两个问题：

* 避免重名覆盖
* 防止暴露用户真实文件名（安全）

---

## 🚀 9. 上传到阿里 OSS

```java
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
```

解释：

* `file.getBytes()`：读取文件二进制内容
* `objectName`：OSS 中对象名/路径
* `aliOssUtil.upload(...)`：

  * 核心上传逻辑：

    * 建立连接
    * 指定 bucket
    * 上传
    * 返回公网 URL
* 返回结果：

```
https://bucket-name.oss-cn-beijing.aliyuncs.com/uuid.png
```

👉 Controller 直接返回 URL 给前端

例如：

* 新建菜品
* 上传图片
* 前端拿到 URL 直接存数据库

---

## ❌ 10. 上传失败日志 & 错误返回

```java
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
```

解释：

* 捕获 `IOException`
* `log.error` 记录异常信息
* 返回统一错误：

```
UPLOAD_FAILED = "文件上传失败"
```

返回 JSON：

```json
{
  "code": 0,
  "msg": "文件上传失败"
}
```

👉 注意：

* 失败不会抛异常到前端
* 而是**优雅返回错误信息**

---

# ⭐ 整体执行流程总结

```
前端选择图片 → 发送表单请求(file)
↓
MultipartFile 自动封装
↓
获取原文件名与后缀
↓
生成 UUID 新文件名
↓
上传到阿里 OSS
↓
返回可访问 URL 给前端
↓
前端保存到数据库并展示图片
```

---

# 🧠 你可以这样在面试里说

> 文件上传采用 MultipartFile 接收，
> 上传到阿里云 OSS，
> 通过 UUID 生成唯一文件名，
> 防止覆盖与信息泄露，
> 上传失败时统一封装错误返回。

👉 这句话 = 面试官非常喜欢的答案 😎

---

## 🚀 你还可以继续选：

1️⃣ 我帮你解释 **AliOssUtil 工具类逐行代码**
2️⃣ 我帮你补 OSS 配置 `application.yml`
3️⃣ 用 **本地 MinIO 替代 OSS**（开发更方便）
4️⃣ 补充前端 VUE 上传示例
5️⃣ 给你出面试题（带答案）


