
```
package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}

```

Great — here Result is being used as the unified response wrapper 
for every API in this controller. 
I’ll explain it based directly on this file 👇

⸻

✅ What Result does here

Every controller method returns:

```Result<...>```

instead of
- raw data
- boolean
- entity object
- Map
- HttpResponse

👉 This ensures all HTTP responses have the same structure:
```
{
  "code": 1,
  "msg": null,
  "data": ...
}
```
or on error:
```
{
  "code": 0,
  "msg": "error message"
}
```
This makes the frontend parsing simple and stable.

⸻

🧩 Look at each method and its Result type

1️⃣ Add Category
```
public Result<String> save(...)
```
- returns only status, no data
- backend uses:

```return Result.success();```


⸻

2️⃣ Page Query

public Result<PageResult> page(...)

- outer = unified wrapper
- inner = pagination data wrapper
- combined response shape:
```
{
  "code": 1,
  "data": {
    "total": 100,
    "records": [ ... ]
  }
}
```

⸻

3️⃣ Delete, Update, Enable/Disable
```
public Result<String> deleteById(Long id)
public Result<String> update(...)
public Result<String> startOrStop(...)
```
👉 return only success indicator

⸻

4️⃣ Query list by type
```
public Result<List<Category>> list(Integer type)

returned JSON example:

{
  "code": 1,
  "data": [
    { "id": 1, "name": "Dish" },
    { "id": 2, "name": "Set meal" }
  ]
}
```

⸻

⭐ Why use Result in controllers?

- Benefit	Explanation
- Unified format	FE doesn’t guess shape
- Easy error handling	Result.error(msg)
- Works with global exception handler	one place to translate exceptions
- Clear success vs failure	code=1 / code=0
- Extensible	can add traceId, time, etc


⸻

🎯 One-sentence interview answer

- The controller uses Result<T> as a unified API wrapper
        so that all endpoints return consistent structures with code, msg, and data,                 simplifying front-end consumption and enabling centralized exception handling.

⸻

If you’d like, I can also help you:
- 🧭 draw the Result + PageResult + Controller flow diagram
- 🛠 write a GlobalExceptionHandler that returns Result.error
- 🧪 give 5 interview questions on unified API response design
