```
package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

```
很好 👍 这是 **苍穹外卖–后台菜品管理 DishController**，而且包含非常重要的一段 **Redis 缓存清理策略**。
我给你做一份 **逐行+逐功能解释版**，你直接当项目笔记或面试复习材料用即可。

---

# 🥗 DishController 逐行详解（含 Redis 缓存一致性）

---

## 1️⃣ 类与注解

```java
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
```

解释：

* `@RestController`

  * 返回 JSON，而不是页面
* `@RequestMapping("/admin/dish")`

  * 接口统一前缀
  * 所有接口都在 `/admin/dish/**`
* `@Api(tags = "菜品相关接口")`

  * Swagger 文档标题
* `@Slf4j`

  * 日志对象 `log` 自动注入

---

## 2️⃣ 注入 Service 与 Redis

```java
@Autowired
private DishService dishService;

@Autowired
private RedisTemplate redisTemplate;
```

解释：

* `DishService`

  * 业务逻辑层
  * 负责：

    * 新增菜品
    * 带口味保存
    * 修改
    * 删除
    * 分页

* `RedisTemplate`

  * 操作 Redis 缓存
  * 用来：
    ✔ 删除缓存
    ✔ 维持缓存一致性

---

## 3️⃣ 新增菜品 + 清缓存

```java
@PostMapping
@ApiOperation("新增菜品")
public Result save(@RequestBody DishDTO dishDTO) {
    log.info("新增菜品：{}", dishDTO);
    dishService.saveWithFlavor(dishDTO);

    //清理缓存数据
    String key = "dish_" + dishDTO.getCategoryId();
    cleanCache(key);

    return Result.success();
}
```

逐行讲：

* `@PostMapping`

  * URL：`POST /admin/dish`
* `@RequestBody DishDTO`

  * JSON -> DTO
* `dishService.saveWithFlavor`

  * 事务操作：

    * 保存 dish
    * 保存 flavor 表

🔥 **重点：缓存一致性**

```
新增菜品 → 某个分类的菜品缓存可能过期 → 立刻清除
```

缓存命名规则：

```
dish_分类id
```

例：

```
dish_5
```

---

## 4️⃣ 菜品分页查询

```java
@GetMapping("/page")
@ApiOperation("菜品分页查询")
public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
    log.info("菜品分页查询:{}", dishPageQueryDTO);
    PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
    return Result.success(pageResult);
}
```

解释：

* URL：`GET /admin/dish/page`
* 参数自动封装为 `DishPageQueryDTO`
* Service 完成分页
* Result 统一返回

没有缓存 👉 后台管理查询性能要求没那么高

---

## 5️⃣ 菜品批量删除 + 清缓存（通杀）

```java
@DeleteMapping
@ApiOperation("菜品批量删除")
public Result delete(@RequestParam List<Long> ids) {
    log.info("菜品批量删除：{}", ids);
    dishService.deleteBatch(ids);

    //将所有的菜品缓存数据清理掉，所有以dish_开头的key
    cleanCache("dish_*");

    return Result.success();
}
```

解释：

* URL：

```
DELETE /admin/dish?ids=1,2,3
```

注意点：

* 接收 List<Long> ids
* 删除菜品并删除 flavor
* 删除 Redis 缓存

🔥 为什么用 `dish_*` 通配？

👉 因为不知道批量删除涉及到多少分类
👉 逐个算太麻烦
👉 直接删所有菜品缓存 ✔

---

## 6️⃣ 根据 id 查询菜品（编辑回显）

```java
@GetMapping("/{id}")
@ApiOperation("根据id查询菜品")
public Result<DishVO> getById(@PathVariable Long id) {
    log.info("根据id查询菜品：{}", id);
    DishVO dishVO = dishService.getByIdWithFlavor(id);
    return Result.success(dishVO);
}
```

解释：

* URL：`GET /admin/dish/123`
* `DishVO` = Dish + 口味列表
* 用于**修改页面数据回显**

---

## 7️⃣ 修改菜品 + 清缓存（通杀）

```java
@PutMapping
@ApiOperation("修改菜品")
public Result update(@RequestBody DishDTO dishDTO) {
    log.info("修改菜品：{}", dishDTO);
    dishService.updateWithFlavor(dishDTO);

    //将所有的菜品缓存数据清理掉，所有以dish_开头的key
    cleanCache("dish_*");

    return Result.success();
}
```

解释：

* URL：`PUT /admin/dish`
* 更新：

  * 菜品表
  * 口味表
* 清全缓存 `dish_*`

原因：

✔ 修改了分类、停售状态、口味
✔ 不确定影响哪个分类
➡ 直接删全部菜品缓存

👉 **这是典型的 cache aside 策略**

---

## 8️⃣ 启售 / 停售 + 清缓存

```java
@PostMapping("/status/{status}")
@ApiOperation("菜品起售停售")
public Result<String> startOrStop(@PathVariable Integer status, Long id) {
    dishService.startOrStop(status, id);

    //将所有的菜品缓存数据清理掉，所有以dish_开头的key
    cleanCache("dish_*");

    return Result.success();
}
```

解释：

* URL：

```
POST /admin/dish/status/0?id=1   停售
POST /admin/dish/status/1?id=1   起售
```

原因：

* 前台用户点餐展示缓存的菜品
* 起售/停售必须立刻生效
* 因此清缓存，不然出现“还能买停售菜品”的问题

---

## 9️⃣ 根据分类查询菜品（这里 **管理端**）

```java
@GetMapping("/list")
@ApiOperation("根据分类id查询菜品")
public Result<List<Dish>> list(Long categoryId) {
    List<Dish> list = dishService.list(categoryId);
    return Result.success(list);
}
```

注意：

* 这是 **管理端**
* 没做缓存
* 小流量操作

👉 前台小程序那边 **有 Redis 缓存**

---

## 🔥 10️⃣ 核心：缓存删除方法（封装得很漂亮）

```java
/**
 * 清理缓存数据
 * @param pattern
 */
private void cleanCache(String pattern){
    Set keys = redisTemplate.keys(pattern);
    redisTemplate.delete(keys);
}
```

逐行解释：

1. `redisTemplate.keys(pattern)`

   * 根据通配符获取所有 key
   * 示例：

```
dish_*  -> 所有菜品缓存
dish_1  -> 分类1所有菜品
```

2. `redisTemplate.delete(keys)`

   * 批量删除 key 集合

✔ 支持单个 key
✔ 支持通配符模式
✔ 支持批量删除

---

# ⭐ 缓存策略总结（面试最爱问）

👉 **写缓存简单，删缓存最难**

本模块采用：

> Cache Aside Pattern（旁路缓存）

规则：

1. 写数据库
2. 删除缓存
3. 下次查询时重新放入缓存（在 C 端查询控制器中）

优点：

* 保证数据一致性
* 逻辑简单可靠

缺点：

* 短暂 cache miss
* 但可接受

---

# 🧠 面试官问你：为什么不用“更新缓存”？

👉 因为：

* 无法确认更新哪几个 key
* 批量修改影响很大
* 更新比删除逻辑复杂很多

**删缓存是工程实践中最正确的方式**

---

# 🎯 你接下来可以让我继续讲：

1️⃣ DishService 实现（含事务 + 口味表）
2️⃣ Redis 缓存前台查询版（select+cache put）
3️⃣ MyBatis XML 映射
4️⃣ 小程序端点餐列表查询逻辑
5️⃣ 分布式缓存一致性如何处理（高级）

