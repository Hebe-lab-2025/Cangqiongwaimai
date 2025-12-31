好呀，我们一个个来，全部给你搞定 ✅

⸻

1️⃣ 模块架构图（Mermaid）
```
graph TD

subgraph sky-pojo  🧱 数据模型模块
    DTO[DTO\n(CategoryDTO, OrdersDTO...)]
    Entity[Entity\n(AddressBook, Dish, Orders...)]
    VO[VO\n(OrderVO, DishVO...)]
end

subgraph sky-common ⚙️ 通用基础模块
    constant[constant\n(状态/消息常量)]
    exception[exception\n(业务异常体系)]
    json[json\n(JacksonObjectMapper)]
    properties[properties\n(JwtProperties, WeChatProperties)]
    result[result\n(Result, PageResult)]
    utils[utils\n(工具类)]
end

subgraph sky-server 🚀 应用服务模块
    controller[controller\n(admin / user)]
    service[service\n(业务逻辑)]
    mapper[mapper\n(MyBatis Mapper)]
    config[config\n(WebMvcConfig, Swagger, JSON配置)]
    interceptor[interceptor\n(JWT 拦截器)]
    task[task\n(定时任务)]
    websocket[websocket\n(实时推送)]
end
```

```
controller --> service
service --> mapper
mapper --> DB[(MySQL)]

controller --> DTO
service --> Entity
controller --> VO

sky-server --> sky-pojo
sky-server --> sky-common
```
你可以直接把这段 Mermaid 贴到支持的 Markdown 环境预览（如 Typora、某些文档工具）。

⸻

2️⃣ 逐行讲一次 Controller → Service → Mapper 流程（以 Category 为例）

2.1 Controller：CategoryController

关键方法：
```
@PostMapping
@ApiOperation("新增分类")
public Result<String> save(@RequestBody CategoryDTO categoryDTO){
    log.info("新增分类：{}", categoryDTO);
    categoryService.save(categoryDTO);
    return Result.success();
}
```
- @RestController：声明这是一个 REST 接口类
- @RequestMapping("/admin/category")：所有路径都以 /admin/category 开头
- @PostMapping：POST 请求 /admin/category 走 save 方法
- @RequestBody CategoryDTO categoryDTO：接收前端 JSON 请求体，封装成 CategoryDTO
- 调用 categoryService.save(categoryDTO)：把业务交给 Service
- Result.success()：用统一返回结构包装响应

👉 职责：只做“接收参数 + 调用 service + 返回结果”

⸻

2.2 Service 接口：CategoryService
```
public interface CategoryService {

    void save(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteById(Long id);

    void update(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    List<Category> list(Integer type);
}
```
- 定义“能做什么”
- 不关心 HTTP、不关心数据库，只关心业务动作：新增、分页、删除、修改、启用/禁用、列表

⸻

2.3 Service 实现：CategoryServiceImpl（核心逻辑）

简化版示例（伪代码风格）：
```
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        // DTO → Entity
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.ENABLE);
        // 调用 Mapper 写数据库
        categoryMapper.insert(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    // 其他方法类似：校验 + 调用 mapper
}
```
- 做 DTO → Entity 转换
- 做业务规则（比如默认启用、检查是否有关联套餐等）
- 调用 CategoryMapper 访问数据库
- 返回 PageResult / List / void

👉 职责：真正放业务逻辑的地方

⸻

2.4 Mapper：CategoryMapper（MyBatis）
```
@Mapper
public interface CategoryMapper {

    void insert(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO dto);

    void deleteById(Long id);

    void update(Category category);

    List<Category> list(Category category);
}
```
配合 mapper/CategoryMapper.xml：
```
<insert id="insert" parameterType="com.sky.entity.Category">
    INSERT INTO category (name, type, sort, status)
    VALUES (#{name}, #{type}, #{sort}, #{status})
</insert>
```
👉 职责：只负责 SQL / 数据库操作，不写业务

⸻

3️⃣ 10 道“分层架构 / 模块划分”面试题（附简短答案）

# 1.	Controller、Service、Mapper 各自的职责是什么？
- Controller：接收 HTTP 请求、参数校验、调用 Service、返回统一结果。
- Service：业务逻辑、事务控制、调用多个 Mapper/外部服务。
- Mapper：专注数据库 CRUD / SQL，不写业务逻辑。

# 2.	为什么要用 DTO 而不是直接用 Entity 接收参数？
- 解耦数据库结构和接口入参
- 避免前端随便改字段直接影响表结构
- DTO 可以专门为某个接口设计字段组合，更灵活。

# 3.	VO 和 DTO 有什么区别？
- DTO：从前端“过来”的数据（请求）
- VO：发“回去”给前端的展示数据（响应）

# 4.	Result 统一返回结构有什么好处？
- 统一 code / msg / data 格式
- 前端只写一套错误处理逻辑
- 方便加 traceId、调试信息、扩展字段。

# 5.	为什么要把异常抽到 sky-common？
- 所有模块共用同一套异常类型
- 全局异常处理可以统一转换为 Result.error()
- 新业务只需扩展子异常类即可。

# 6.	多模块项目中，谁依赖谁？
- sky-server 依赖 sky-pojo（实体、DTO、VO）
- sky-server 依赖 sky-common（工具、异常、配置）
- sky-pojo、sky-common 不依赖 sky-server，避免循环依赖。

# 7.	为什么把 Entity 单独放到 sky-pojo？
- 复用性高：将来如果有另一个服务要用这些实体/DTO/VO，可以直接依赖 pojo 模块
- 清晰：数据模型和业务实现分开。
	
# 8.	分页为什么要单独有一个 PageResult？
- 前端统一接收 total + records
- 后端所有分页接口风格一致，减少前端适配成本。

# 9.	Interceptor / Filter 通常放在哪个模块？职责是什么？
- 放在 sky-server 的 interceptor 包
- 做登录校验、JWT 校验、日志、权限、跨域等横切逻辑。

# 10.	如果以后要加一个新模块（比如后台任务服务），你会怎么拆？
- 新建模块 sky-job 或 sky-task
- 依赖 sky-pojo + sky-common
- 专门做定时任务 / 异步处理，和 sky-server 解耦。

⸻

4️⃣ 如何自己新建一个多模块 SpringBoot 项目（思路版）

# 1.	创建父工程（parent）
- 使用 Maven 建一个空工程，比如 sky-project
- packaging = pom
- 在父 pom 里统一：SpringBoot 版本、依赖管理、插件。

# 2.	在父工程下创建子模块
- sky-common：通用类、异常、配置、自定义 JSON、工具类
- sky-pojo：DTO / Entity / VO
- sky-server：真正的 SpringBoot 启动模块（SkyApplication 所在）

# 3.	pom 依赖关系

- sky-common：只依赖 spring-boot-starter、lombok 等基础库
- sky-pojo：只依赖 lombok 等
- sky-server：
- <dependency> sky-common </dependency>
- <dependency> sky-pojo </dependency>
- 还有 Web / MyBatis / Redis / Swagger 等

# 4.	启动类放在 sky-server
```
@SpringBootApplication
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
    }
}
```

⸻

5️⃣ 面试官问：“请介绍一下你的项目架构？”——参考回答

- Our project follows a classic multi-module layered architecture.
- We have three main Maven modules:
- sky-pojo for data models like DTO, Entity and VO,
- sky-common for shared utilities, exception hierarchy, JSON config and properties binding,
- and sky-server as the main Spring Boot application.

- Inside sky-server, we use a standard Controller–Service–Mapper structure:
    controllers handle HTTP requests and responses with unified Result<T> wrappers,
    services contain the core business logic and transaction management,
    and mappers use MyBatis to interact with the database.

- We also use interceptors for JWT authentication,
    a custom Jackson ObjectMapper for date/time formats,
    and Swagger to generate API documentation for admin and user groups.
- This separation makes the codebase easier to
    maintain, test, and extend when new features or services are added.

你可以再准备一个中文版本：

我们的项目是典型的多模块分层架构。
- Maven 里分成三个模块：sky-pojo 做 DTO/Entity/VO 数据模型，
- sky-common 放通用的异常、工具类、配置，
- sky-server 是真正的 SpringBoot 应用。

- 在 sky-server 里面采用 Controller–Service–Mapper 三层：
- Controller 负责接收请求、返回统一的 Result；
- Service 里写具体业务和事务控制；
- Mapper 用 MyBatis 操作数据库。
- 同时还有 JWT 拦截器、自定义 Jackson 时间格式、Swagger 接口文档等。
- 这样分层之后，每一层职责清晰，后期扩展和维护都比较方便。

⸻

如果你愿意，下一步我可以帮你：
- 🔍 挑一个具体的 OrderController / OrderServiceImpl / OrderMapper，逐行带你读一遍
- 🧭 给你画一张 “从浏览器发请求到数据库更新”完整时序图
- 🧪 出一套 “请画出你项目结构” + “为什么这样拆模块” 的模拟面试对话
