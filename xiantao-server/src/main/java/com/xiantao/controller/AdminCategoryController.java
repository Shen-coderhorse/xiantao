package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.entity.Category;
import com.xiantao.service.CategoryService;
import com.xiantao.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<CategoryVO>> getCategoryList() {
        List<CategoryVO> list = categoryService.getCategoryList();
        return Result.success(list);
    }

    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("创建成功", category);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("删除成功", null);
    }
}
