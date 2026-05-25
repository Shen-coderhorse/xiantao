package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.entity.Category;
import com.xiantao.service.CategoryService;
import com.xiantao.service.UserService;
import com.xiantao.vo.CategoryVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<List<CategoryVO>> getCategoryList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }
        List<CategoryVO> list = categoryService.getCategoryList();
        return Result.success(list);
    }

    @PostMapping
    public Result<Category> createCategory(HttpServletRequest request, @RequestBody Category category) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }
        categoryService.save(category);
        return Result.success("创建成功", category);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(HttpServletRequest request, @PathVariable Long id, @RequestBody Category category) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }
        category.setId(id);
        categoryService.updateById(category);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }
        categoryService.removeById(id);
        return Result.success("删除成功", null);
    }
}
