package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.service.CategoryService;
import com.xiantao.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<CategoryVO>> getCategoryList() {
        List<CategoryVO> list = categoryService.getCategoryList();
        return Result.success(list);
    }
}
