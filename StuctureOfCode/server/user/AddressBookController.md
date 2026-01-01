很好，这段是**用户端地址簿（收货地址）Controller**，我用清晰中文帮你完整讲解 👇

---

## 🧩 这是做什么的？

提供 **收货地址管理接口**，典型用于外卖/商城 App：

* 查询地址列表
* 新增地址
* 查询单个地址
* 修改地址
* 删除地址
* 设置默认地址
* 查询默认地址

路径统一前缀：

```
/user/addressBook
```

---

## 🧠 关键点先看懂

* 使用 `BaseContext.getCurrentId()` 获取当前登录用户 ID
  👉 说明你们用 **ThreadLocal 存 userId**
* 返回统一包装 `Result`
* `AddressBookService` 负责业务逻辑
* Swagger 注解用于接口文档

---

## 🧭 逐个接口功能解释

---

### ✅ 1）查询当前用户全部地址

```java
@GetMapping("/list")
public Result<List<AddressBook>> list()
```

流程：

1. 构建查询对象 AddressBook
2. 填充 userId 为当前用户
3. 调用 `addressBookService.list()`
4. 返回地址列表

👉 只查当前用户的数据，避免跨用户越权

---

### 🆕 2）新增地址

```java
@PostMapping
public Result save(@RequestBody AddressBook addressBook)
```

* 前端 JSON → AddressBook
* 交给 service 保存
* 返回 success

👉 userId 通常在 service 里补全更安全

---

### 🔍 3）根据 id 查询地址

```java
@GetMapping("/{id}")
public Result<AddressBook> getById(@PathVariable Long id)
```

典型用途：

* 编辑地址时回显数据

---

### ✏️ 4）根据 id 修改地址

```java
@PutMapping
public Result update(@RequestBody AddressBook addressBook)
```

service 中需注意：

* 校验 userId 是否匹配当前用户
* 防止越权修改别人地址

---

### ⭐ 5）设置默认地址

```java
@PutMapping("/default")
public Result setDefault(@RequestBody AddressBook addressBook)
```

正确业务逻辑应该是：

1. 把当前用户所有地址 is_default = 0
2. 把当前选择地址 is_default = 1

👉 数据库建议唯一约束：**每个用户仅 1 个默认地址**

---

### 🗑 6）根据 id 删除地址

```java
@DeleteMapping
public Result deleteById(Long id)
```

⚠️ 注意点：

* 这里没有 `@PathVariable` 或 `@RequestParam`
* 默认用 request param 方式

例如：

```
DELETE /user/addressBook?id=1
```

---

### ⭐ 7）查询默认地址

```java
@GetMapping("default")
public Result<AddressBook> getDefault()
```

SQL 意思：

```sql
select * from address_book
where user_id = ? and is_default = 1
```

处理逻辑：

* 找到 1 条 → 返回
* 没找到 → 返回错误信息

---

## ⚠️ 可以改进的点（真实项目必须注意）

### 🚨 1）越权安全问题

必须校验：

* 地址所属 userId == 当前用户 userId

否则可随便删别人地址

---

### 🌀 2）默认地址唯一性

推荐：

* DB 层加唯一索引

```
(user_id, is_default=1)
```

---

### ♻ 3）删除默认地址行为

建议规则：

* 若删除默认地址 → 自动选择一个新默认
* 或 强制要求先切换默认

---

### 💡 4）幂等 & 乐观锁

适用场景：

* 并发修改默认地址
* app 多端提交

---

## 🧪 如果你需要，我可以继续提供：
```
👉 **带详细中文注释版本代码**
👉 MySQL 地址表结构
👉 MyBatis / JPA mapper 示例
👉 幂等 + 事务 + 默认地址唯一实现
👉 接口 Postman 调试示例
👉 常见面试题总结（围绕本代码）
```
你想要哪一个？

很好 👍 我按你的 6 个需求一项项给到，直接可用、可拷贝改造成项目代码。

---

# ✅ 1) 带**详细中文注释版本** AddressBookController

```java
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {

        // 创建查询条件
        AddressBook addressBook = new AddressBook();

        // 获取当前登录用户id（ThreadLocal 中存储）
        addressBook.setUserId(BaseContext.getCurrentId());

        // 根据 userId 查询该用户的所有地址
        List<AddressBook> list = addressBookService.list(addressBook);

        // 统一返回结构
        return Result.success(list);
    }

    /**
     * 新增地址
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook) {

        // 绑定当前用户（防止前端伪造 userId）
        addressBook.setUserId(BaseContext.getCurrentId());

        // 保存到数据库
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {

        AddressBook addressBook = addressBookService.getById(id);

        // 统一返回
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {

        // 禁止修改非本人的地址（安全校验）
        addressBook.setUserId(BaseContext.getCurrentId());

        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {

        // Service 内包含两步：
        // ① 将当前用户所有地址 is_default = 0
        // ② 将当前传入的地址 is_default = 1
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {

        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1); // 默认地址标记
        addressBook.setUserId(BaseContext.getCurrentId());

        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }
        return Result.error("没有查询到默认地址");
    }
}
```

---

# ✅ 2) MySQL 地址表结构（建议版）

```sql
CREATE TABLE address_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户id',
    consignee VARCHAR(64) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail VARCHAR(200) COMMENT '详细地址',
    label VARCHAR(20) COMMENT '标签（家、公司、学校）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认 1是 0否',
    create_time DATETIME,
    update_time DATETIME
);
```

👉 **强烈建议唯一约束（保证唯一默认地址）**

```sql
ALTER TABLE address_book
ADD UNIQUE KEY uk_user_default (user_id, is_default);
```

---

# ✅ 3) MyBatis Mapper 示例

### XML 方式

```xml
<select id="list" parameterType="AddressBook" resultType="AddressBook">
    select * from address_book
    where user_id = #{userId}
</select>
```

---

### MyBatis-Plus（推荐最简洁）

```java
QueryWrapper<AddressBook> wrapper = new QueryWrapper<>();
wrapper.eq("user_id", userId);
return list(wrapper);
```

---

# ✅ 4) 幂等 + 事务 + 唯一默认地址实现（核心）

```java
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper mapper;

    @Transactional
    @Override
    public void setDefault(AddressBook addressBook) {

        Long userId = BaseContext.getCurrentId();

        // 1. 将该用户所有地址设为非默认
        AddressBook reset = new AddressBook();
        reset.setIsDefault(0);

        UpdateWrapper<AddressBook> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId);
        mapper.update(reset, wrapper);

        // 2. 将当前地址设为默认
        addressBook.setIsDefault(1);
        mapper.updateById(addressBook);
    }
}
```

### 幂等性体现

👉 即使多次点击 “设为默认”
👉 最终状态始终一致
👉 **事务保证数据一致性**

---

# ✅ 5) Postman 调试示例

### 查询列表

```
GET /user/addressBook/list
```

---

### 新增地址

```
POST /user/addressBook
```

Body JSON：

```json
{
  "consignee": "张三",
  "phone": "13800000000",
  "detail": "北京市 海淀区 xxx小区 3单元502",
  "label": "家"
}
```

---

### 设置默认地址

```
PUT /user/addressBook/default
```

```json
{
  "id": 12
}
```

---

### 删除地址

```
DELETE /user/addressBook?id=12
```

---

### 查询默认地址

```
GET /user/addressBook/default
```

---

# ✅ 6) 常见面试题总结（围绕这段代码）

### ❓如何保证每个用户只有一个默认地址？

> 数据库唯一约束 + 事务

---

### ❓如何防止越权删除别人地址？

> 后端判断 userId = 当前用户

---

### ❓为什么要用 ThreadLocal 存用户 ID？

> 过滤器统一解析 token
> Controller 无需反复传参
> 线程安全

---

### ❓幂等性的实现方式？

- ✔ 唯一键
- ✔ 去重 token
- ✔ 状态判断
- ✔ 乐观锁

---

### ❓为什么默认地址修改必须加事务？

否则：

* 第一步取消默认成功
* 第二步设置默认失败
  👉 用户无默认地址 ❌

---

### ❓如何防止并发覆盖默认地址？

方案：

* 悲观锁 `for update`
* 乐观锁 version 字段
* Redis 分布式锁（集群）

---

## 🎁 如果你愿意，我还能继续给你：

* - ✔ 完整 Service + Mapper 代码
* - ✔ MyBatis-Plus 全套写法
* - ✔ Redis 缓存默认地址实现
* - ✔ Swagger 文档效果截图
* - ✔ Vue / 微信小程序前端接口调用
* - ✔ 单元测试 JUnit 示例

👉 你想要哪个？


太棒了 👍 我把你要的 6 项全部补全，直接可用、可拷贝改造成项目代码结构。

---

# ✅ 1) **完整 Service 接口 + 实现类 + Mapper 接口**

## - ✔ AddressBookService 接口

```java
public interface AddressBookService {

    List<AddressBook> list(AddressBook addressBook);

    void save(AddressBook addressBook);

    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void deleteById(Long id);

    void setDefault(AddressBook addressBook);

    AddressBook getDefault();
}
```

---

## - ✔ AddressBookServiceImpl 实现类（含事务 & 唯一默认地址）

```java
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.delete(id);
    }

    /**
     * 设置默认地址（幂等 + 事务）
     */
    @Transactional
    @Override
    public void setDefault(AddressBook addressBook) {

        Long userId = BaseContext.getCurrentId();

        // 1. 取消当前用户所有默认地址
        addressBookMapper.clearDefault(userId);

        // 2. 设置当前地址为默认
        addressBook.setIsDefault(1);
        addressBookMapper.updateDefault(addressBook.getId());
    }

    @Override
    public AddressBook getDefault() {
        return addressBookMapper.getDefault(BaseContext.getCurrentId());
    }
}
```

---

## - ✔ Mapper 接口

```java
@Mapper
public interface AddressBookMapper {

    List<AddressBook> list(AddressBook addressBook);

    void insert(AddressBook addressBook);

    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void delete(Long id);

    void clearDefault(Long userId);

    void updateDefault(Long id);

    AddressBook getDefault(Long userId);
}
```

---

# ✅ 2) **MyBatis-Plus 全套写法（推荐）**

### - ✔ 继承 BaseMapper 即可

```java
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
```

---

### - ✔ Service 继承 MP 的 ServiceImpl

```java
@Service
public class AddressBookServiceImpl
        extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {
}
```

---

### - ✔ 查询当前用户所有地址

```java
public List<AddressBook> listByUser() {
    return lambdaQuery()
            .eq(AddressBook::getUserId, BaseContext.getCurrentId())
            .list();
}
```

---

### - ✔ 设置默认地址（MyBatis-Plus 风格）

```java
@Transactional
public void setDefault(AddressBook addressBook) {

    Long userId = BaseContext.getCurrentId();

    // 全部改成非默认
    lambdaUpdate()
            .eq(AddressBook::getUserId, userId)
            .set(AddressBook::getIsDefault, 0)
            .update();

    // 指定地址改为默认
    lambdaUpdate()
            .eq(AddressBook::getId, addressBook.getId())
            .set(AddressBook::getIsDefault, 1)
            .update();
}
```

---

# ✅ 3) **Redis 缓存默认地址实现**

👉 目的：减少 DB 压力
👉 key 设计：`address:default:userId`

---

### - ✔ 查询默认地址（含缓存）

```java
@Autowired
private StringRedisTemplate redisTemplate;

@Override
public AddressBook getDefault() {

    Long userId = BaseContext.getCurrentId();
    String key = "address:default:" + userId;

    // 1）先查缓存
    String cache = redisTemplate.opsForValue().get(key);
    if (cache != null) {
        return JSON.parseObject(cache, AddressBook.class);
    }

    // 2）查数据库
    AddressBook address = lambdaQuery()
            .eq(AddressBook::getUserId, userId)
            .eq(AddressBook::getIsDefault, 1)
            .one();

    // 3）写入缓存
    if (address != null) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(address));
    }

    return address;
}
```

---

### - ✔ 更新默认地址时：删除缓存

```java
redisTemplate.delete("address:default:" + BaseContext.getCurrentId());
```

---

# ✅ 4) Swagger 文档效果（接口摘要示例）

| 接口名    | 方法     | 路径                        |
| ------ | ------ | ------------------------- |
| 查询地址列表 | GET    | /user/addressBook/list    |
| 新增地址   | POST   | /user/addressBook         |
| 根据id查  | GET    | /user/addressBook/{id}    |
| 修改地址   | PUT    | /user/addressBook         |
| 删除地址   | DELETE | /user/addressBook         |
| 设置默认   | PUT    | /user/addressBook/default |
| 查询默认   | GET    | /user/addressBook/default |

📌 你已使用：

```java
@Api(tags="C端地址簿接口")
@ApiOperation("xxx")
```

👉 打开 `http://localhost:8080/doc.html` 或 `/swagger-ui.html` 即可看到

---

# ✅ 5) Vue / 微信小程序 **前端接口调用示例**

### - ✔ Vue axios

```js
// 查询地址列表
axios.get('/user/addressBook/list')

// 设置默认
axios.put('/user/addressBook/default', { id: 12 })

// 新增地址
axios.post('/user/addressBook', form)
```

---

### - ✔ 微信小程序（wx.request）

```js
wx.request({
  method: 'GET',
  url: '/user/addressBook/default',
  success: res => {
    this.setData({
      defaultAddress: res.data.data
    })
  }
})
```

---

# ✅ 6) 单元测试 JUnit 示例

```java
@SpringBootTest
public class AddressBookTest {

    @Autowired
    private AddressBookService addressBookService;

    @Test
    public void testSetDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(10L);
        addressBookService.setDefault(addressBook);
    }

    @Test
    public void testList() {
        List<AddressBook> list = addressBookService.listByUser();
        System.out.println(list);
    }
}
```

---

## 🎯 你现在拥有

- ✔ Controller
- ✔ Service
- ✔ Mapper
- ✔ MyBatis-Plus 写法
- ✔ Redis 缓存
- ✔ Swagger API 描述
- ✔ 前端请求示例
- ✔ JUnit 测试

---

如果你愿意，我还能继续补充：

* 📌 MySQL 建库脚本 + 索引优化
* 🧠 线程安全 BaseContext 源码
* 🔒 Token 登录过滤器
* 🧾 完整表 ER 图
* 🧭 面试官追问高频 20 题 + 答案

