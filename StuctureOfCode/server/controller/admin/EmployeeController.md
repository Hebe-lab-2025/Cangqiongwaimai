
```
package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}

```
很好 👍 这段是 **苍穹外卖后台——员工管理 EmployeeController**，包含：

* 登录（JWT 令牌）
* 退出
* 新增员工
* 分页查询
* 启用 / 禁用账号
* 根据 id 查询
* 编辑员工信息

我给你做 **逐行详细讲解版 + 登录流程与 JWT 机制说明**，直接当项目复习资料用。

---

# 👨‍💻 EmployeeController 逐行详解

---

## 1️⃣ 类与注解

```java
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {
```

含义：

* `@RestController`

  * 返回 JSON，不返回页面
* `@RequestMapping("/admin/employee")`

  * 统一前缀 `/admin/employee`
* `@Slf4j`

  * 自动生成 `log`
* `@Api`

  * Swagger 文档显示组名

---

## 2️⃣ 注入 Service 和 JWT 配置信息

```java
@Autowired
private EmployeeService employeeService;

@Autowired
private JwtProperties jwtProperties;
```

解释：

* `EmployeeService`

  * 处理：

    * 登录验证
    * 新增员工
    * 查询与分页
    * 启用禁用
    * 编辑
* `JwtProperties`

  * 读取 YAML 中 JWT 配置
  * 包含：

    * 密钥 secretKey
    * 过期时间 ttl
    * token 存储头字段等

---

# 🔐 3️⃣ 登录接口 — 核心（生成 JWT）

```java
@PostMapping("/login")
@ApiOperation(value = "员工登录")
public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
    log.info("员工登录：{}", employeeLoginDTO);

    Employee employee = employeeService.login(employeeLoginDTO);
```

逐行解释：

* URL：

  ```
  POST /admin/employee/login
  ```
* `@RequestBody EmployeeLoginDTO`

  * 接受 JSON 登录参数
  * 包括 username + password
* `employeeService.login`

  * 核心逻辑（在 Service 层）：

    * 校验用户名是否存在
    * 校验密码是否正确（MD5 比对）
    * 校验账号是否被禁用
  * 返回正确员工实体 Employee

---

### 🧠 登录成功 → 生成 JWT 令牌

```java
    //登录成功后，生成jwt令牌
    Map<String, Object> claims = new HashMap<>();
    claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
```

解析：

* 声明负载 claims（JWT 中 payload）
* 放入员工 id

`JwtClaimsConstant.EMP_ID` 例如：

```
"empId"
```

也就是 token 中会保存当前员工 id

---

### 🛠️ 创建 Token

```java
    String token = JwtUtil.createJWT(
            jwtProperties.getAdminSecretKey(),
            jwtProperties.getAdminTtl(),
            claims);
```

参数解释：

| 参数             | 代表含义       |
| -------------- | ---------- |
| adminSecretKey | 签名用的密钥     |
| adminTtl       | token 过期时间 |
| claims         | 令牌中存入的内容   |

JWT 三部分：

```
Header.Payload.Signature
```

例如生成类似：

```
eyJhbGciOiJIUzI1NiJ9.xxx.yyy
```

客户端之后每次请求携带：

```
Authorization: Bearer <token>
```

后台网关或过滤器拦截器解析 JWT，确认身份 ✔

---

### 🎁 封装返回信息

```java
    EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
            .id(employee.getId())
            .userName(employee.getUsername())
            .name(employee.getName())
            .token(token)
            .build();
```

解释：

* VO = 返回给前端的对象
* builder 构造 → Lombok

返回内容包含：

* 员工 id
* 登录名
* 真实姓名
* JWT 令牌

---

### 🟢 返回成功

```java
    return Result.success(employeeLoginVO);
}
```

前端收到：

```json
{
  "code":1,
  "data":{
    "id":1,
    "userName":"admin",
    "name":"管理员",
    "token":"xxx.yyy.zzz"
  }
}
```

---

# 🚪 4️⃣ 退出登录

```java
@PostMapping("/logout")
@ApiOperation("员工退出")
public Result<String> logout() {
    return Result.success();
}
```

解释：

* 只是返回成功
* 为什么不删 token？

👉 token 是**无状态的**
👉 服务端不存，不需要删除
👉 只要前端丢弃 token 即退出

如果想做强制退出：

* 可将 token 加入黑名单（Redis）
* 或变更签名 secretKey

---

# 🆕 5️⃣ 新增员工

```java
@PostMapping
@ApiOperation("新增员工")
public Result save(@RequestBody EmployeeDTO employeeDTO){
    log.info("新增员工：{}",employeeDTO);
    employeeService.save(employeeDTO);
    return Result.success();
}
```

解释：

* URL：`POST /admin/employee`
* 参数：EmployeeDTO
* Service 处理业务：

  * 默认密码 123456（MD5 加密）
  * 状态为启用
  * 设置创建时间 / 修改时间
  * 设置创建人 / 修改人（从 ThreadLocal BaseContext 获取）

---

# 📄 6️⃣ 员工分页查询

```java
@GetMapping("/page")
@ApiOperation("员工分页查询")
public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
    log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
    PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
    return Result.success(pageResult);
}
```

说明：

* URL：`GET /admin/employee/page`
* 参数自动封装 EmployeePageQueryDTO
* PageHelper 做分页
* PageResult 包含：

  * 总条数
  * 当前页数据列表

---

# 🚦 7️⃣ 启用 / 禁用员工账号

```java
@PostMapping("/status/{status}")
@ApiOperation("启用禁用员工账号")
public Result startOrStop(@PathVariable Integer status,Long id){
    log.info("启用禁用员工账号：{},{}",status,id);
    employeeService.startOrStop(status,id);
    return Result.success();
}
```

解释：

* URL 示例：

```
POST /admin/employee/status/1?id=3   启用
POST /admin/employee/status/0?id=3   禁用
```

功能：

* 修改 employee 表 status 字段
* 禁用账号后：

  * 再登录 → 直接抛异常
  * 登录接口已经校验

---

# 🔍 8️⃣ 根据 id 查询员工信息

```java
@GetMapping("/{id}")
@ApiOperation("根据id查询员工信息")
public Result<Employee> getById(@PathVariable Long id){
    Employee employee = employeeService.getById(id);
    return Result.success(employee);
}
```

用途：

* 修改员工信息表单回显

---

# ✏️ 9️⃣ 编辑员工信息

```java
@PutMapping
@ApiOperation("编辑员工信息")
public Result update(@RequestBody EmployeeDTO employeeDTO){
    log.info("编辑员工信息：{}", employeeDTO);
    employeeService.update(employeeDTO);
    return Result.success();
}
```

说明：

* 修改信息（不改密码）
* 修改人/修改时间更新
* 账号名是否允许修改由 Service 控制

---

# 🔥 登录 + JWT 认证完整流程（总览）

```
前端提交账号密码
↓
Controller 调用 employeeService.login
↓
校验密码、状态
↓
生成 JWT（包含 empId）
↓
返回 token
↓
前端保存 token（localStorage / header）
↓
每次请求带 token
↓
后端拦截器解析 token，识别当前员工
↓
放入 BaseContext
↓
业务层取当前员工 id
```

---

# 🧠 面试可说的亮点

✔ JWT 无状态登录
✔ Token 不存数据库
✔ 统一 Result 返回格式
✔ DTO / VO 分层清晰
✔ 启用禁用与登录绑定
✔ ThreadLocal 保存当前登录员工 id

---

如果你愿意，下一步可以继续：

1️⃣ **EmployeeService.login 源码逐行解析**
2️⃣ **JWT 生成与解析工具类 JwtUtil 讲解**
3️⃣ **登录拦截器 LoginCheckFilter 逐行讲**（非常重要）
4️⃣ 演示 token 被禁用后登录失败的完整流程
