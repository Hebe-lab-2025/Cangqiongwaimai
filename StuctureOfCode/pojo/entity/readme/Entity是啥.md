很好，这个问题问得非常关键 👍
我们就以你选中的 AddressBook 为例，直接解释——什么是 Entity（实体类）。

⸻

✅ 什么是 Entity？

Entity = 实体类 = 数据库表在 Java 中的映射对象

👉 通俗理解：
- 	数据库里一行 = Java 中一个对象
- 	数据库里一张表 = Java 中一个实体类（Entity）

⸻

🧭 AddressBook 就是实体类（Entity）

你这段代码：

```public class AddressBook implements Serializable {```

表示：
- 	它是 地址簿表的实体
- 	每个字段基本对应数据库中的一列

⸻

🗄️ 对应数据库可能是这样的一张表

address_book
### 📮 address_book 表结构

| 字段名 | 含义 |
|--------|------|
| `id` | 主键 ID |
| `user_id` | 用户 ID |
| `consignee` | 收货人姓名 |
| `phone` | 收货人手机号 |
| `sex` | 性别 |
| `province_code` | 省份编码 |
| `province_name` | 省份名称 |
| `city_code` | 城市编码 |
| `city_name` | 城市名称 |
| `district_code` | 区/县编码 |
| `district_name` | 区/县名称 |
| `detail` | 详细地址 |
| `label` | 地址标签（如：家/公司） |
| `is_default` | 是否为默认地址 |



⸻

🧩 Java 对象 与 数据库 行/列 的关系

### 🧩 Java 对象 与 数据库 行/列 的关系映射

| Java 字段 | 含义 | 数据库列 |
|-----------|------|-----------|
| `id` | 主键 | `id` |
| `userId` | 用户 ID | `user_id` |
| `consignee` | 收货人 | `consignee` |
| `phone` | 手机号 | `phone` |
| `detail` | 详细地址 | `detail` |
| `isDefault` | 是否默认地址 | `is_default` |


👉 ORM 框架（MyBatis/JPA）会自动帮你做映射

⸻

🧠 Entity 的特点（和 DTO/VO 不同）
| 名称 | 主要用途 |
|------|-----------|
| `Entity` | 映射数据库表 |
| `DTO` | 接收前端入参 |
| `VO` | 返回给前端展示 |


👉 Entity 更多是：后端 ↔ 数据库

⸻

🧃 生活类比（特别好理解）

📱 通讯录 = 数据库表
👤 每个联系人 = 一行数据
🧾 “联系人”对象 = Entity

⸻

🔍 解释你代码里的几个注解
```
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
```
作用：
- 	@Data 自动生成 getter/setter/toString
- 	@Builder 支持链式创建对象
- 	@NoArgsConstructor 无参构造
- 	@AllArgsConstructor 全参构造

👉 都是 Lombok，减少样板代码

⸻

🛠 这个 Entity 在项目里一般用在：

-  MyBatis Mapper 查询
-  Service 层业务处理
-  存入数据库
-  从数据库读出来

例如：

```AddressBook address = addressBookMapper.getById(1L);```


⸻

🎯 一句话总结（面试可背）

- Entity 是用于映射数据库表的领域对象，

- 通常与表结构一一对应，

- 主要在持久化层和业务层之间传递数据，

- 不直接暴露给前端，也不承担参数接收职责。

⸻

如果你愿意，我还能继续帮你：
- 	🧭 画一张 DTO / Entity / VO 区别图
- 	🔍 帮你打开对应 address_book 表 SQL
- 	🛠 教你 MyBatis 如何把行映射成 Entity
- 	🧪 出 5 道“Entity vs DTO vs VO”面试题
