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

