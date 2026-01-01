
```
package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        //构造redis中的key，规则：dish_分类id
        String key = "dish_" + categoryId;

        //查询redis中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(list != null && list.size() > 0){
            //如果存在，直接返回，无须查询数据库
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        //如果不存在，查询数据库，将查询到的数据放入redis中
        list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}

```
这段代码是 **Spring Boot 全局异常处理器**，用来统一拦截并处理项目运行过程中的异常 👇

---

## 🧩 它在项目中的作用

* 集中捕获 Controller 层抛出的异常
* 统一返回前端格式一致的 `Result`
* 记录日志，方便排错
* 把数据库异常转成**业务友好提示**

---

## 🧠 关键注解说明

### ✔ `@RestControllerAdvice`

* 作用于所有 `@RestController`
* 等价于： `@ControllerAdvice + @ResponseBody`
* 专门做 **全局统一异常处理**

### ✔ `@ExceptionHandler`

* 指定该方法处理哪种异常类型
* 匹配到就不会继续往外抛

### ✔ `@Slf4j`

* 提供 `log.error()` 打印异常日志

---

## 🔍 逐段解析代码

---

### ✅ 1）捕获业务异常 BaseException

```java
@ExceptionHandler
public Result exceptionHandler(BaseException ex){
    log.error("异常信息：{}", ex.getMessage());
    return Result.error(ex.getMessage());
}
```

👉 使用场景
例如：手动校验失败、资源不存在等

```java
throw new BaseException("用户名已存在");
```

👉 返回前端：

```json
{
  "code": 0,
  "msg": "用户名已存在"
}
```

---

### ✅ 2）处理 SQL 唯一约束异常（重复键）

```java
@ExceptionHandler
public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
```

典型报错：

```
Duplicate entry 'zhangsan' for key 'employee.idx_username'
```

代码逻辑：

1. 拿到异常 message
2. 判断是否包含 “Duplicate entry”
3. 按空格切割字符串
4. 拿到重复的字段值
5. 拼成友好提示

```java
String[] split = message.split(" ");
String username = split[2];
String msg = username + MessageConstant.ALREADY_EXISTS;
```

👉 返回：

```
zhangsan已存在
```

---

### ⚠️ 否则情况（未知 SQL 错误）

```java
return Result.error(MessageConstant.UNKNOWN_ERROR);
```

例如：

* 外键约束失败
* NOT NULL 约束
* 语法错误

统一提示：

```
未知错误
```

---

## 🧾 MessageConstant 里一般是什么

常见定义：

```java
public class MessageConstant {
    public static final String ALREADY_EXISTS = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
}
```

---

## 🛠 整体处理流程（文字流程图）

```
Controller 执行出错
        ↓
抛出异常
        ↓
被 GlobalExceptionHandler 捕获
        ↓
记录日志
        ↓
组装 Result 错误返回
        ↓
前端显示友好提示
```

---

## ⭐ 优点总结

* 统一异常出口 👍
* 代码更干净 👍
* 前端接口风格一致 👍
* 与业务逻辑解耦 👍
* 日志集中便于排查 👍

---

## 💡 可改进点（生产级）

### ✔ 返回 HTTP 状态码

现在永远是 200
建议：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
```

---

### ✔ 支持更多异常类型

* NullPointerException
* MethodArgumentNotValidException
* AccessDeniedException
* RuntimeException 兜底

---

### ✔ 打印堆栈 trace 方便排错

```java
log.error("异常：", ex);
```

---

### ✔ 处理 MySQL 其他错误

* 外键冲突
* 非法日期
* 数据长度超限

---

## 🎁 如果你愿意，我还能帮你：

* 写 **BaseException 源码**
* 完整 `MessageConstant` 定义
* 加入 **参数校验异常处理**
* 返回 **国际化错误信息（中/英）**
* Spring Validation (`@Valid`) 联动异常处理
* 画异常处理体系图（Controller → Advice → Result）

你想要继续哪一块？





