
我打开了你这个 GitHub 仓库的首页结构，仓库实际上 **没有 README 内容**（所以没有已有 Markdown 介绍）([GitHub][1])

但我可以根据它的目录给你一个 **标准的项目 README Markdown 模板**，你粘到仓库根目录下 `README.md` 就能变成专业文档：

---

```markdown
# Cangqiongwaimai

> 🍔 苍穹外卖 — 项目代码结构 & 异常设计 & 常量约定（示例说明）

## 📁 目录结构

```

StuctureOfCode/
├─ common/
│  ├─ Notes/
│  │  └─ exceptionNotes.md
├─ images/
│  └─ exception.jpg
├─ ...

````

## 🧠 设计规范（核心说明）

### ✅ 常量约定

| 用法 | 示例 |
|------|------|
| 常量类 | `AutoFillConstant`、`JwtClaimsConstant` |
| 优势 | 统一管理、IDE 提示、便于修改 |

---

### 🔒 public static final

| 关键字 | 含义 |
|--------|------|
| `public` | 全局可访问 |
| `static` | 类级别，无需 new |
| `final` | 一次赋值，不可改 |

---

### 🧭 常量对比

| 场景 | 用 enum | 用常量 |
|------|---------|--------|
| 取值是固定集合 | ✔ 强烈推荐 | ✖ 不推荐 |
| 想防止非法值 | ✔ 类型安全 | ✖ 不做到 |
| 只是一串文本/数字 | ✖ 不需要 enum | ✔ 常量最简单 |

---

## 🚨 异常类结构

以下所有业务异常继承自统一 `BaseException`：

```mermaid
classDiagram
direction LR

class RuntimeException
class BaseException {
    +BaseException()
    +BaseException(String message)
}

class AccountLockedException
class AccountNotFoundException
class AddressBookBusinessException
class DeletionNotAllowedException
class LoginFailedException
class OrderBusinessException
class ShoppingCartBusinessException
class PasswordEditFailedException
class PasswordErrorException
class SetmealEnableFailedException
class UserNotLoginException

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
````

---

## 📦 包说明

| 包名            | 存放内容                 |
| ------------- | -------------------- |
| `constant`    | 文本/状态常量              |
| `enumeration` | 枚举类型                 |
| `context`     | ThreadLocal 当前用户等上下文 |
| `exception`   | 自定义业务异常类             |

---

## 💡 异常设计优势

| 优点     | 解释          |
| ------ | ----------- |
| 统一异常结构 | 全局异常处理器统一返回 |
| 语义清晰   | 类名表达业务含义    |
| 方便扩展   | 每个业务域独立异常类  |

---

## 📌 示例业务异常表

| 异常类                           | 场景          |
| ----------------------------- | ----------- |
| `PasswordErrorException`      | 密码错误        |
| `DeletionNotAllowedException` | 不允许删除（关联数据） |

---

## 🛠 开发 & 贡献

欢迎贡献！请遵循以下规范：

* 代码规范：统一异常 + 常量分类
* 提交分支：feature/说明
* 合并请求：PR 至 `main`

```

---

如果你愿意，我还能进一步为你自动生成：

✅ **README 含运行示例 & 构建说明**  
✅ **贡献指南（CONTRIBUTING.md）**  
✅ **代码块 + 错误码表**  
✅ **GitHub 图标 badges**  

只要告诉我你的项目技术栈、运行方式、常见命令等细节，我就能补全。
::contentReference[oaicite:1]{index=1}
```

[1]: https://github.com/Hebe-lab-2025/Cangqiongwaimai "GitHub - Hebe-lab-2025/Cangqiongwaimai"
