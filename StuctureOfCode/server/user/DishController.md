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

很好，这段是 **C 端菜品浏览接口（带 Redis 缓存）**，我用清晰、分步骤中文完整讲解 👇

---

## 🧩 这段代码是干嘛的？

👉 根据 **分类 id** 查询菜品列表
👉 并对结果做 **Redis 缓存**（提升性能）

常见场景：

* 外卖 app 点餐页
* 根据“川菜/饮料/套餐”等分类展示菜品
* 附带口味（辣度、冰量等）

---

## 🧠 关键点速览

包含 3 个核心功能：

* 根据分类 id 查菜品
* 只查 **起售状态菜品（上架中）**
* Redis 缓存提高性能（避免频繁查 DB）

---

## 🧾 逐行解释 Controller 逻辑

### ① 注解说明

```java
@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
```

含义：

* `@RestController` → 返回 JSON
* Bean 名特意取 `userDishController`
  👉 为避免与商家端 DishController 冲突
* `@Slf4j` 日志
* Swagger 分组说明

---

### ② 依赖注入

```java
@Autowired
private DishService dishService;

@Autowired
private RedisTemplate redisTemplate;
```

用途：

* `DishService`
  👉 查询菜品 + 口味
* `RedisTemplate`
  👉 做缓存

---

## 🔍 核心接口：根据分类查询菜品

```java
@GetMapping("/list")
public Result<List<DishVO>> list(Long categoryId)
```

### 👉 Step 1：构造 Redis Key

```java
String key = "dish_" + categoryId;
```

缓存规则：

```
dish_分类ID
例：dish_3
```

---

### 👉 Step 2：查询 Redis 缓存

```java
List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
```

如果缓存存在：

```java
if(list != null && list.size() > 0){
    return Result.success(list);
}
```

📌 直接返回，不查数据库
📌 大量减少 DB 压力，优化 QPS

---

### 👉 Step 3：缓存未命中 → 查数据库

```java
Dish dish = new Dish();
dish.setCategoryId(categoryId);
dish.setStatus(StatusConstant.ENABLE);
```

关键点：

* **只查起售状态 ENABLE**
* 下架菜品不会显示给用户

---

### 👉 Step 4：查询带口味的菜品

```java
list = dishService.listWithFlavor(dish);
```

说明：

* DishVO 包含：

  * 菜品基本信息
  * 口味（flavor）
* 如：

  * 辣/不辣
  * 冰/去冰
  * 加糖/无糖

---

### 👉 Step 5：写入 Redis 缓存

```java
redisTemplate.opsForValue().set(key, list);
```

🎯 目的：

* 提升性能
* 缓解数据库压力
* 用户访问更快（尤其首页）

---

### 👉 Step 6：返回结果

```java
return Result.success(list);
```

---

## ⭐ Redis 缓存策略讲清楚

### 🔹 读流程（缓存优先）

```
查 Redis
  ↓命中
直接返回
  ↓未命中
查数据库
写入 Redis
返回
```

这叫：

👉 **缓存穿透防护**
👉 **Cache Aside Pattern（旁路缓存模式）**

---

## ⚠️ 建议优化（面试 & 实战重点）

### ✔ 给缓存设置 TTL 防止脏数据

```java
redisTemplate.opsForValue().set(key, list, 1, TimeUnit.HOURS);
```

---

### ✔ 加分类维度清缓存（写操作时）

当：

* 新菜品上架
* 菜品停售
* 菜品信息更新

应该：

👉 删除该分类缓存

```java
redisTemplate.delete("dish_" + categoryId);
```

---

### ✔ 预防缓存击穿（热点分类）

可加：

* 分布式锁
* double-check 缓存

---

### ✔ 序列化安全

生产环境建议：

```java
RedisTemplate<String, Object>
```

并配置 JSON 序列化

---

## 🧪 Postman 调试示例

### 请求

```
GET /user/dish/list?categoryId=3
```

### 返回

```json
[
  {
    "id": 101,
    "name": "宫保鸡丁",
    "price": 28.00,
    "flavors": [
      {"name":"辣度","value":"微辣"},
      {"name":"加料","value":"花生"}
    ]
  }
]
```

---

## 🧠 可能的面试官提问

### ❓为什么使用 Redis 缓存菜品？

👉 高频读
👉 低频写
👉 典型缓存场景

---

### ❓什么时候需要清除缓存？

* 菜品新增
* 菜品停售
* 菜品修改
* 菜品分类变化

👉 B 端操作时清缓存

---

### ❓为什么不用 session 缓存？

* Redis 支持集群
* 高并发读写
* 跨服务共享缓存

---

### ❓为什么只查询 ENABLE 状态菜品？

👉 给 C 端用户看
👉 下架 & 售罄不展示

---

## 🎁 如果你愿意，我可以继续提供：

* ✔ DishService.listWithFlavor 源码
* ✔ MyBatis-Plus 查询实现
* ✔ Redis 缓存雪崩/击穿/穿透解决方案
* ✔ 套餐与菜品联动缓存
* ✔ Spring Cache 注解版实现（@Cacheable）
* ✔ 全链路图（Controller → Service → DB → Redis）

