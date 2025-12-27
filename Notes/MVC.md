# Spring Boot 与前端交互 & MVC 架构说明（菜品项目）

## 一、Spring Boot 项目是怎么跟前端"说话"的？

前后端靠 **HTTP + JSON** 交互：

### 1. 前端（浏览器 / Vue 页面）发送请求

-   axios.get('/dish/list?categoryId=1')
-   axios.post('/employee/login', { username:'张三', password:'123456'
    })

### 2. 请求到达 Spring Boot

-   内置 Web 服务器：Tomcat
-   统一入口：DispatcherServlet

### 3. DispatcherServlet 找 Controller 方法

-   根据 URL + Method
-   /dish/list → DishController.list()

### 4. Controller 拿参数 → 调用 Service

-   @PathVariable
-   @RequestParam
-   @RequestBody

### 5. Service 调用 Mapper 访问数据库

### 6. 返回 Java 对象 → 自动转 JSON

### 7. 前端渲染页面

------------------------------------------------------------------------

## 二、MVC 架构

-   Model：Entity/DTO/VO
-   View：Vue 页面
-   Controller：@RestController

分层：

-   Controller
-   Service
-   Mapper/Repository
-   Model

------------------------------------------------------------------------

## 三、示例代码

### 1）Controller

``` java
@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/list")
    public List<Dish> list(
            @RequestParam Long categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer status) {

        return dishService.listByCategoryAndStatus(categoryId, status);
    }
}
```

### 2）Service

``` java
@Service
public class DishService {

    private final DishMapper dishMapper;

    public DishService(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public List<Dish> listByCategoryAndStatus(Long categoryId, Integer status) {
        return dishMapper.selectByCategoryAndStatus(categoryId, status);
    }
}
```

### 3）前端 axios

``` js
axios.get('/dish/list', {
  params: { categoryId: 1, status: 1 }
}).then(res => { this.dishList = res.data })
```

------------------------------------------------------------------------

## 四、时序图（简化文字版）

用户 → 浏览器/Vue → Controller → Service → Mapper → MySQL

------------------------------------------------------------------------

## 五、接口文档模板

-   URL
-   Method
-   Params
-   JSON Body
-   Response

------------------------------------------------------------------------

## 六、GET /dish/list 示例

请求：

GET /dish/list?categoryId=1&status=1

响应：

``` json
[
  { "id":1, "name":"鱼香肉丝", "status":1 }
]
```



______

Great — here is the clean, well-structured Markdown file you asked for.
You can copy-paste it as springboot-mvc-interaction.md 👍

⸻

Spring Boot × Frontend Interaction & MVC Architecture

✅ 1. How does Spring Boot talk to the frontend?

Frontend & backend communicate using HTTP + JSON.


🔄 Basic interaction flow
	1.	Frontend sends HTTP request
	•	Example: Vue + axios

axios.get('/dish/list?categoryId=1')
axios.post('/employee/login', {
  username: '张三',
  password: '123456'
})

	2.	Request reaches Spring Boot
	•	Built-in Tomcat receives request
	•	Request enters DispatcherServlet (Spring MVC entry)
	3.	DispatcherServlet finds matching Controller
	•	Matches by URL + HTTP method
	•	Example: /dish/list → DishController.list()
	4.	Controller gets parameters, calls Service
	•	Parameter sources:
	•	@PathVariable
	•	@RequestParam
	•	@RequestBody
	•	Controller role = receive → delegate, not write SQL
	5.	Service executes business logic
	•	validation
	•	transaction
	•	domain rules
	6.	Service calls Mapper/DAO
	•	Example: MyBatis mapper

dishMapper.selectByCategoryAndStatus(...)

	7.	MySQL returns data
	8.	Controller returns Java object
	•	Spring Boot converts result into JSON using Jackson
	9.	Frontend receives JSON and updates UI

⸻

🧭 Overall flow picture

Vue / Browser
   ↓
HTTP request
   ↓
Controller (Spring Boot)
   ↓
Service (Business Logic)
   ↓
Mapper / Repository
   ↓
MySQL Database
   ↑
Return result
   ↑
JSON Response
   ↑
Frontend renders data


⸻

🧩 2. MVC in your project

MVC = Model – View – Controller

Layer	Meaning	Example in your project
Model	data objects	Dish, Employee, DTO, VO
View	UI	Vue pages
Controller	handles request	DishController, EmployeeController

✔ Your actual structure

controller   → Controller layer
service      → Business logic
mapper       → Database access (MyBatis)
entity/dto/vo → Model layer


⸻

🍜 3. Example walkthrough — DishController

✅ Controller

@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    // GET /dish/list?categoryId=1&status=1
    @GetMapping("/list")
    public List<Dish> list(
            @RequestParam Long categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer status) {

        return dishService.listByCategoryAndStatus(categoryId, status);
    }
}

✅ Service

@Service
public class DishService {

    private final DishMapper dishMapper;

    public DishService(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public List<Dish> listByCategoryAndStatus(Long categoryId, Integer status) {
        return dishMapper.selectByCategoryAndStatus(categoryId, status);
    }
}

✅ Mapper (pseudo MyBatis)

public interface DishMapper {
    List<Dish> selectByCategoryAndStatus(Long categoryId, Integer status);
}

✅ SQL

SELECT *
FROM dish
WHERE category_id = #{categoryId}
  AND status = #{status};

✅ Vue axios call

axios.get('/dish/list', {
  params: { categoryId: 1, status: 1 }
}).then(res => {
  this.dishList = res.data
})


⸻

🕒 4. Sequence diagram (ASCII)

User → Frontend → Controller → Service → Mapper → MySQL
 |        |           |           |           |
 | click  |           |           |           |
 |------->|           |           |           |
           send HTTP GET /dish/list?categoryId=1
           ---------------------------------------->
                        Controller finds method
                        -------------------------->
                                  call Service
                                  ---------------->
                                           call SQL
                                           -------->
                                                  execute & return rows
                                           <--------
                                  return List<Dish>
                        <--------------------------
          return JSON
<----------------------------------------
UI renders list


⸻

📑 5. API documentation template (reusable)

✔ General template

URL

/dish/list

Method

GET

Request params

Name	Type	Required	Where	Description
categoryId	Long	Yes	query	category ID
status	Integer	No	query	1 enable / 0 disable

Request example

GET /dish/list?categoryId=1&status=1

Response example

[
  {
    "id": 1,
    "name": "鱼香肉丝",
    "price": 28.00,
    "status": 1
  }
]


⸻

🎤 6. Interview one-sentence answer

The frontend calls Spring Boot REST APIs via HTTP and JSON.
DispatcherServlet routes requests to Controllers, Controllers delegate business logic to Services,
Services call Mappers to access MySQL, and results are returned as JSON for the frontend to render.

⸻

🧪 7. Your Test01 example (simple case)

Code

@RestController
@RequestMapping("Test01")
public class Test01 {

    @GetMapping("test01")
    public String test01(String name){
        return "Hello Hebe " + name;
    }
}

Test URL

http://localhost:8080/Test01/test01?name=MeiMei

Output

Hello Hebe MeiMei


⸻

✅ You can ask me next:
	•	🔍 Walk your actual DishController line-by-line
	•	🧭 Draw sequence diagram as image instead of ASCII
	•	🛠 Generate Swagger docs
	•	🚀 Write pagination API /dish/page
	•	🧑‍🍳 Design full dish module CRUD (Controller + Service + Mapper)

Just say: “继续 dish 分页接口” or “走一遍 DishController 真实代码”
____
