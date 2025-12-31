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

✅ Q1. 为什么要设计一个 BaseException，让所有业务异常继承它？

👉 一句话答案：因为“统一管理 + 一次捕获所有业务异常”

🧠 通俗理解

没有 BaseException：到处是各种异常，难收拾

有 BaseException：所有业务异常都有同一个“爸爸”

✅ 这样做的好处

全部业务异常 👉 同一个大类型

全局异常处理器只写一次 @ExceptionHandler(BaseException.class)

所有业务异常统一格式返回

代码不乱，逻辑清晰

🗣️ 面试可说这样

我会设计一个 BaseException 继承 RuntimeException，
所有业务异常继承 BaseException。
这样全局异常处理器只需要捕获 BaseException 一次，
就可以统一处理所有业务异常，做到风格一致、代码复用和语义清晰。


⸻

## Q2. 你怎么解释“子类可以当作父类使用”（多态），用你的异常体系举例？

参考回答：

With inheritance, a child class object can be referenced by a parent type.
For example, BaseException e = new OrderBusinessException("order error"); is valid,
because OrderBusinessException extends BaseException.
In our global exception handler, we use @ExceptionHandler(BaseException.class) to catch all subclasses,
such as OrderBusinessException, ShoppingCartBusinessException, and AccountLockedException.
This is a practical use of polymorphism in our exception system.

✅ Q2. 什么叫“子类可以当作父类使用”？用你的异常举例

👉 关键词：多态

🧠 通俗解释

儿子是人类

叫“人”也没问题
👉 但是“人”不一定都是儿子

✅ 在你的异常体系中
BaseException e = new OrderBusinessException("order error");


✔ 合法
✔ 因为：

OrderBusinessException 继承 BaseException

🧰 在全局异常处理中的真实用法
@ExceptionHandler(BaseException.class)
public Result handle(BaseException ex) { ... }


👉 能捕获：

OrderBusinessException

ShoppingCartBusinessException

AccountLockedException

PasswordErrorException

…

🗣️ 面试可这样说

多态意味着子类对象可以用父类类型来引用。
在我的项目中，BaseException 是父类，
所有业务异常是子类。
全局异常处理器使用 @ExceptionHandler(BaseException.class)，
就可以一次性捕获所有业务异常，这是多态的具体应用。


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

✅ Q3. 业务异常 和 系统异常 怎么区分？
🧠 通俗解释
类型	举例	给用户的感觉
业务异常	订单状态错误、密码错	你操作不符合业务规则
系统异常	空指针、数据库挂了	系统自己坏了
✅ 我的做法

业务异常 👉 BaseException 子类

系统异常 👉 让它抛出去，统一兜底

🛠 处理策略

业务异常：提示清晰业务信息

系统异常：记录日志 ❗ 不把细节暴露给用户

🗣️ 面试可说

业务异常是规则被违反，例如订单状态不合法；
系统异常是非预期错误，例如空指针、数据库连接失败。
我会用 BaseException 体系处理业务异常，
系统异常使用 Exception 兜底，记录日志但不暴露内部信息。

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

✅ Q4. 为什么不全用 RuntimeException？

👉 因为：太笼统，没有语义

❌ 全用 RuntimeException 会怎样？

无法区分：

登录失败

订单错误

购物车为空

日志没法分类

很难维护

✅ 自定义异常的好处

LoginFailedException

OrderBusinessException

UserNotLoginException

👉 一看名字就知道发生什么
👉 代码 = 自文档

🗣️ 面试可说

只用 RuntimeException 太粗糙，语义不清晰。
我定义了多个业务异常类来表达具体业务含义，
既提升了可读性，也方便做精细化日志与分类处理。

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

```
✅ Q5. 在 Spring 项目里如何全局处理自定义异常？

👉 关键：@RestControllerAdvice

🛠 实际套路

建 BaseException

所有业务异常继承它

写 GlobalExceptionHandler

🌟 示例思路（不用写代码也能讲）

业务异常处理器：

@ExceptionHandler(BaseException.class)
→ 返回明确的业务提示


系统异常处理器：

@ExceptionHandler(Exception.class)
→ 记录日志，返回通用错误

🗣️ 面试可说

我会用 @RestControllerAdvice + @ExceptionHandler
捕获 BaseException 统一处理业务异常，
另外加一个 Exception 兜底，处理未知系统异常，
保证接口返回格式统一，同时保护系统内部信息。

🎁 如果你愿意，我可以继续帮你：

⭐ 把这些整理成 一页面试速记 PDF 风格内容

⭐ 直接写成 英文长回答，可背诵

⭐ 给你一套 面试官和你的模拟问答
```

```
太好了 👍
我先给你一份 **英文长回答（可直接在面试里说）**，把 Q1–Q5 串成一段完整回答；再附上 **中文速记版**，你可以背诵或做笔记。

---

# ✅ 英文长回答（可直接背诵，在面试中自然流畅）

In my projects, I usually design a unified exception system instead of using only RuntimeException.

First, I create a BaseException that extends RuntimeException, and then all business exceptions, such as OrderBusinessException, ShoppingCartBusinessException, and AccountLockedException, extend BaseException. This gives me a single root type for all business-level errors. In Spring, I combine this with a GlobalExceptionHandler using @RestControllerAdvice and @ExceptionHandler(BaseException.class), so I can handle all business exceptions in one place with a consistent API response format.

Inheritance also lets me use polymorphism in practice. A child exception can be referenced as its parent type, so for example, BaseException e = new OrderBusinessException("order error") is valid. More importantly, any subclass of BaseException will automatically be caught by the handler for BaseException. This is a real-world example of polymorphism applied in exception handling.

I also clearly distinguish business exceptions from system exceptions. Business exceptions indicate business rule violations, such as invalid order status or incorrect password, and I throw subclasses of BaseException for these. Unexpected technical errors such as NullPointerException or database connection failures are treated as system exceptions and handled by a more general Exception handler. Business exceptions return meaningful messages, while system exceptions are logged in detail but not exposed to clients.

I do not use only RuntimeException because it is too coarse-grained. Using dedicated custom exception classes allows me to express intent directly in the type, improves readability, supports finer-grained logging and handling, and makes the codebase self-documenting. Overall, this approach keeps controllers clean, centralizes error handling, improves maintainability, and helps build a robust and consistent API.

---

# 🧠 中文速记版（超好背）

### ⭐ 我为什么要 BaseException？

* 统一“业务异常的大类”
* 全局异常处理器只捕获一次就够
* 返回格式统一，代码更整洁

### ⭐ 多态怎么用在异常？

* 子类当父类用
  `BaseException e = new OrderBusinessException(...)`
* `@ExceptionHandler(BaseException.class)`
  👉 自动兜住所有子异常

### ⭐ 区分两类异常

| 类型   | 处理方式                    |
| ---- | ----------------------- |
| 业务异常 | BaseException 子类，返回清晰提示 |
| 系统异常 | Exception 兜底，详细记录日志     |

### ⭐ 为什么不用 RuntimeException？

* 语义不清晰 ❌
* 无法细分业务 ❌
* 日志不好分类 ❌
  👉 自定义异常 = 代码自解释 ✔

### ⭐ 在 Spring 里怎么配合？

* `@RestControllerAdvice`
* `@ExceptionHandler(BaseException.class)` 业务异常
* `@ExceptionHandler(Exception.class)` 系统异常兜底

---

# 📄 你要哪个下一步？

我可以继续帮你（任选一项或多选）👇

1️⃣ 生成 **一页纸 PDF 风格总结文本**
2️⃣ 生成 **Markdown 版“异常设计总结”直接放到 GitHub**
3️⃣ 做 **模拟面试对话**（我当面试官问你）
4️⃣ 给你 **代码模板**：BaseException + GlobalExceptionHandler + Result
5️⃣ 生成 **中英双语对照版** 🎓
```


```
太棒了 👍
我一次性把 **1–5 全部给你准备好**，你可直接用、直接粘 GitHub、直接背面试。

---

# 🧾 1️⃣ 一页纸 PDF 风格总结文本（可直接生成 PDF）

> 🎯 建议：直接复制以下内容到 Word/Notion → 导出 PDF
> 结构已经按“一页纸”设计

---

## 🚀 Exception System Design – One-Page Summary

### 🎯 Goal

Provide a unified, readable and maintainable exception-handling mechanism for business applications.

---

## 🧩 Core Idea

* Define a **BaseException**
* All **business exceptions extend BaseException**
* Use **GlobalExceptionHandler** to统一处理
* Separate **business exceptions** and **system exceptions**
* Ensure API error responses remain **consistent and meaningful**

---

## 🏗 Exception Hierarchy

```
RuntimeException
    ↓
BaseException
    ↓
Business sub-exceptions
    • OrderBusinessException
    • ShoppingCartBusinessException
    • AccountLockedException
    • PasswordErrorException
    • UserNotLoginException
```

---

## 🧠 Why BaseException?

* One root type for business errors
* Enables **polymorphism**
* Allows **global capture with single handler**
* Improves code reuse & clarity
* Keeps controller code clean

---

## 🔍 Business vs System Exceptions

| Type               | Examples                           | Handling Strategy                          |
| ------------------ | ---------------------------------- | ------------------------------------------ |
| Business Exception | order status invalid, login failed | return clear business message              |
| System Exception   | NPE, DB down, server crash         | log full detail, generic message to client |

---

## 🧭 Key Concepts

* Inheritance
* Polymorphism
* Separation of concerns
* Global exception handling
* API consistency

---

## 🛠 Implementation Recipe

1. Create `BaseException extends RuntimeException`
2. Create domain-specific subclasses
3. Implement `@RestControllerAdvice`
4. Add

   * `@ExceptionHandler(BaseException.class)`
   * `@ExceptionHandler(Exception.class)`
5. Return unified `Result<T>` response format

---

## ⭐ Benefits

* Semantic clarity
* Centralized handling
* Safer API exposure
* Maintainability
* Extensibility

---

## 🧾 Takeaway Statement

> A unified exception system with BaseException + GlobalExceptionHandler
> helps isolate business errors from system errors,
> keeps controllers simple, and ensures a consistent API contract.

---

# 📚 2️⃣ Markdown 版《异常设计总结》（直接放 GitHub）

````markdown
# 🚨 Exception Design Summary

## 🎯 Goal
Unify business exception handling and keep API responses consistent.

---

## 🧩 Exception Hierarchy

```mermaid
classDiagram
direction TB

RuntimeException <|-- BaseException

BaseException <|-- OrderBusinessException
BaseException <|-- ShoppingCartBusinessException
BaseException <|-- AccountLockedException
BaseException <|-- PasswordErrorException
BaseException <|-- UserNotLoginException
````

---

## ✅ Why BaseException?

* Unified business exception root
* One handler for all business errors
* Enables polymorphism
* Better readability and maintainability

---

## 🧠 Polymorphism Example

```java
BaseException e = new OrderBusinessException("order error");
```

@ExceptionHandler(BaseException.class)
→ automatically catches all subclasses

---

## 🔍 Business vs System Exception

| Type     | Examples                    | Handling                    |
| -------- | --------------------------- | --------------------------- |
| Business | login failed, order invalid | return clear message        |
| System   | NPE, DB down                | log & generic error message |

---

## 🛠 Global Exception Handling Pattern

* `@RestControllerAdvice`
* `@ExceptionHandler(BaseException.class)` → business
* `@ExceptionHandler(Exception.class)` → system fallback

---

## 🧾 Key Benefit Summary

| Benefit         | Explanation                              |
| --------------- | ---------------------------------------- |
| Code reuse      | shared construction logic                |
| Unified type    | all business errors extend BaseException |
| Clear semantics | class name shows meaning                 |
| Easy extension  | add new subclass only                    |

---

````

---

# 🎤 3️⃣ 模拟面试对话（你是候选人）

**面试官：** Why did you design a BaseException in your project?  
**你：**  
I wanted to have a unified root type for all business exceptions,  
so I created a BaseException extending RuntimeException,  
and made all business exceptions extend it.  
This allows my global exception handler to catch BaseException once  
and handle all business errors consistently.

---

**面试官：** How does your design use polymorphism?  
**你：**  
Polymorphism allows child objects to be referenced as parent types.  
In my project, any subclass of BaseException  
can be caught by `@ExceptionHandler(BaseException.class)`.  
So OrderBusinessException, ShoppingCartBusinessException, etc.  
are all handled automatically by the same method.

---

**面试官：** How do you distinguish business and system exceptions?  
**你：**  
Business exceptions are rule violations such as invalid order status.  
System exceptions are unexpected technical failures.  
I throw BaseException subclasses for business errors  
and let system exceptions fall into a generic handler  
that logs the full stack trace and returns a safe message.

---

**面试官：** Why not just use RuntimeException?  
**你：**  
Because it is too coarse-grained.  
Custom exception classes let me express business intent clearly,  
enable fine-grained handling and logging, and keep code self-documenting.

---

# 🧩 4️⃣ 代码模板（BaseException + GlobalHandler + Result）

```java
// BaseException
public class BaseException extends RuntimeException {
    public BaseException() {}
    public BaseException(String message) {
        super(message);
    }
}
````

```java
// Example business exception
public class OrderBusinessException extends BaseException {
    public OrderBusinessException(String msg) {
        super(msg);
    }
}
```

```java
// Unified result wrapper
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data){
        Result<T> r = new Result<>();
        r.code = 0;
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String msg){
        Result<T> r = new Result<>();
        r.code = -1;
        r.msg = msg;
        return r;
    }
}
```

```java
// Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Result<String> handleBusiness(BaseException ex){
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleSystem(Exception ex){
        ex.printStackTrace();
        return Result.error("Internal server error");
    }
}
```

---

# 🌏 5️⃣ 中英双语速记版

### 📌 核心理念

* EN: Unified business exception system
* CN: 统一的业务异常体系

### 📌 BaseException 作用

* EN: single root type for business errors
* CN: 所有业务异常的“统一父类”

### 📌 多态

* EN: child type used as parent reference
* CN: 子类可以当父类用

### 📌 业务 vs 系统异常

* EN: rule violation vs system failure
* CN: 业务规则错误 vs 系统崩溃

### 📌 Spring 处理方式

* @RestControllerAdvice
* @ExceptionHandler(BaseException.class)
* @ExceptionHandler(Exception.class)

---

## 🎁 想继续吗？

我还可以帮你：

* ✍ 帮你把这一页排版成真正 **PDF 文件内容结构**
* 🧭 给你 **面试 10 连环追问 + 答案**
* 🗂 结合你现在项目代码结构“定制答案”
```
