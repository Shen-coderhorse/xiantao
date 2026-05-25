package com.xiantao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiantao.common.Result;
import com.xiantao.dto.ProductDTO;
import com.xiantao.service.ProductService;
import com.xiantao.service.UserService;
import com.xiantao.vo.ProductVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<Page<ProductVO>> getProductList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        Page<ProductVO> page = productService.getAdminProductList(pageNum, pageSize, categoryId, status, keyword);
        return Result.success(page);
    }

    @PostMapping
    public Result<ProductVO> createProduct(HttpServletRequest request, @Valid @RequestBody ProductDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        ProductVO vo = productService.adminCreateProduct(dto);
        return Result.success("创建成功", vo);
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        ProductVO vo = productService.adminUpdateProduct(id, dto);
        return Result.success("修改成功", vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        productService.adminDeleteProduct(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(HttpServletRequest request, @PathVariable Long id, @RequestParam Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        productService.adminUpdateStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
