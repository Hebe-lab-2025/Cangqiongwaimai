好，我们把这一块直接做成**“可以抄进项目 + 可以背去面试”**的版本 👇

⸻

1️⃣ UML 类图（Mermaid）

你可以直接把这段贴到支持 Mermaid 的地方（如 markdown 预览）：
```
classDiagram
    class RuntimeException

    class BaseException {
        +BaseException()
        +BaseException(String message)
    }

    class AccountLockedException {
        +AccountLockedException()
        +AccountLockedException(String msg)
    }

    class AccountNotFoundException {
        +AccountNotFoundException(String msg)
    }

    class AddressBookBusinessException {
        +AddressBookBusinessException(String msg)
    }

    class DeletionNotAllowedException {
        +DeletionNotAllowedException(String msg)
    }

    class LoginFailedException {
        +LoginFailedException(String msg)
    }

    class OrderBusinessException {
        +OrderBusinessException(String msg)
    }

    class ShoppingCartBusinessException {
        +ShoppingCartBusinessException(String msg)
    }

    class PasswordEditFailedException {
        +PasswordEditFailedException(String msg)
    }

    class PasswordErrorException {
        +PasswordErrorException(String msg)
    }

    class SetmealEnableFailedException {
        +SetmealEnableFailedException(String msg)
    }

    class UserNotLoginException {
        +UserNotLoginException(String msg)
    }

    RuntimeException <|-- BaseException

    BaseException <|-- AccountLockedException
    BaseException <|-- AccountNotFoundException
    BaseException <|-- AddressBookBusinessException
    BaseException <|-- DeletionNotAllowedException
    BaseException <|-- LoginFailedException
    BaseException <|-- OrderBusinessException
    BaseException <|-- ShoppingCartBusinessException
    BaseException <|-- PasswordEditFailedException
    BaseException <|-- PasswordErrorException
    BaseException <|-- SetmealEnableFailedException
    BaseException <|-- UserNotLoginException
```
你也可以只保留你项目里真正有的异常类，按需删减。

⸻

2️⃣ 全局异常处理器 GlobalExceptionHandler 示例

假设你有一个通用返回类：com.sky.result.Result，结构大概是：
```
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> error(String msg) { ... }
}
```


那么全局异常处理器可以这样写：
```
package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（统一继承 BaseException）
     */
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException ex) {
        // 打印业务异常日志（warn 级别即可）
        log.warn("Business exception: {}", ex.getMessage());

        // 返回统一错误结构
        return Result.error(ex.getMessage());
    }

    /**
     * 兜底异常处理：处理未预期的系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        // 打印系统异常详细信息（error 级别）
        log.error("System exception: ", ex);

        // 不把系统内部信息暴露给前端
        return Result.error("服务器内部错误，请稍后重试");
    }
}
```
📌 这里体现了：
	•	继承 + 多态：所有继承 BaseException 的子类，统一被 handleBaseException 捕获
	•	业务异常 & 系统异常分层处理
	•	日志级别区分：业务 warn，系统 error

```以后你只要 throw new OrderBusinessException(...) / throw new UserNotLoginException(...)，都会走 handleBaseException。```

⸻

# 3️⃣ 5 道「异常体系 / 多态 / 继承」面试题（含参考答案）

## Q1. 为什么要设计一个 BaseException，让所有业务异常继承它？

参考回答：

In my projects I usually create a BaseException that extends RuntimeException,
and then make all business exceptions extend BaseException.
This gives me a single root type for all business errors,
so my GlobalExceptionHandler can catch BaseException once and handle all business cases consistently.
It keeps error handling unified, reduces duplicate code, and still keeps clear semantics through specific subclasses.

⸻

## Q2. 你怎么解释“子类可以当作父类使用”（多态），用你的异常体系举例？

参考回答：

With inheritance, a child class object can be referenced by a parent type.
For example, BaseException e = new OrderBusinessException("order error"); is valid,
because OrderBusinessException extends BaseException.
In our global exception handler, we use @ExceptionHandler(BaseException.class) to catch all subclasses,
such as OrderBusinessException, ShoppingCartBusinessException, and AccountLockedException.
This is a practical use of polymorphism in our exception system.

⸻

## Q3. 业务异常和系统异常，你是怎么区分和处理的？

参考回答：

I separate business exceptions from system exceptions.
All business rule violations, like “order status error” or “cart is empty”,
throw subclasses of BaseException.
These are caught by a specific handler and returned as a clear business error message to the client.
For unexpected system-level errors, like NullPointerException or database failures,
I let them fall into a more general Exception handler that logs the full stack trace
and returns a generic “internal server error” message.
This way, clients get meaningful messages for business issues,
while internal errors are logged but not exposed.

⸻

## Q4. 为什么不直接用 RuntimeException，而要定义很多自定义异常类？

参考回答：

Using only RuntimeException makes error handling too coarse-grained.
You cannot distinguish a login failure from an order status error just by the type.
By defining custom exceptions like LoginFailedException, OrderBusinessException, and UserNotLoginException,
I can express the business intent directly in the type,
and I can also apply more fine-grained handling if needed.
For example, I could log login failures differently from payment errors.
It improves readability, maintainability, and makes the code self-documenting.

⸻

## Q5. 在 Spring 项目中，你如何用全局异常处理配合自定义异常？

参考回答：

In Spring, I use @RestControllerAdvice combined with @ExceptionHandler.
First, I create a BaseException and let all business exceptions extend it.
Then I write a GlobalExceptionHandler like:
@ExceptionHandler(BaseException.class) to handle all business errors
and return Result.error(ex.getMessage()).
I also add another @ExceptionHandler(Exception.class) as a fallback for unknown system errors.
This pattern centralizes error handling, keeps controllers clean,
and ensures that the API always returns a consistent error format.

⸻

如果你愿意，下一步我可以帮你：
	•	把你现在的 异常体系 + GlobalExceptionHandler + Result 组合成一页“项目总结”
	•	或者帮你写一段 “我在项目里是怎么设计异常系统的” 英文长回答，可以直接用在面试里 🌟
