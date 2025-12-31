```
package com.sky.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
```
Great — this is your unified API response wrapper 👍
I’ll explain what each part does and how it’s used in real projects.

⸻

✅ What Result<T> is for

It standardizes every backend response into one structure:
```
{
  "code": 1,
  "msg": "OK",
  "data": {...}
}
```
So frontend can always read:
- code → success or failure
- msg → message
- data → real payload

⸻

🧩 Code breakdown (simple & clear)

1️⃣ Class definition
```
public class Result<T> implements Serializable

Meaning:
- generic type T → can hold any data type
- implements Serializable → can be transmitted (HTTP, Redis, MQ, etc.)
```
Examples of T:
``
| T | Data type |
|---|------------|
| `Result<T>` | single object result |
| `Result<List<T>>` | list result |
| `Result` | no data (only status and message) |

``

⸻

2️⃣ Fields
```
private Integer code; // 1 success, 0/others fail
private String msg;   // error message
private T data;       // returned business data
```
So:

| code | meaning |
|------|----------|
| `1` | success |
| `0` | failure |
| others | custom error types |



⸻

3️⃣ Success with no data
```
public static <T> Result<T> success() {
    Result<T> result = new Result<T>();
    result.code = 1;
    return result;
}
```
Usage:

```return Result.success();```

→ returned JSON

```{ "code": 1 }```


⸻

4️⃣ Success with data
```
public static <T> Result<T> success(T object)

Usage:

return Result.success(user);

JSON:

{
  "code": 1,
  "data": { ... }
}
```

⸻

5️⃣ Error result

```
public static <T> Result<T> error(String msg)

Usage:

return Result.error("password incorrect");

JSON:

{
  "code": 0,
  "msg": "password incorrect"
}
```

⸻

⭐ Why static methods?

Advantages:
- easy to call
- avoids new Result<>() everywhere
- always consistent structure
- prevents misuse

This pattern = static factory method

⸻

🧭 Typical Controller use
```
@GetMapping("/user")
public Result<User> getUser() {
    User user = userService.getUser();
    return Result.success(user);
}
```
Error example:

```return Result.error("User not found");```


⸻

🛠 Recommended small improvements

✔ add constants for code
```
public static final int SUCCESS = 1;
public static final int ERROR = 0;
```
✔ or use enum

```public enum ResultCode { SUCCESS, ERROR }```

✔ add fluent builder

⸻

🎯 Interview answer (high-score, short)

- Result<T> is a generic unified API response wrapper
      containing status code, message, and data.

- It uses static factory methods
      to create success or error responses,
      avoids duplicate code, and provides
      consistent REST API format across the project.

⸻

If you’d like next, I can help you with:
- 🧭 draw a Result UML diagram
- 🛡 integrate it with GlobalExceptionHandler
- 🚀 add i18n message support
- 🧪 practice interview questions on API result wrapping

  
