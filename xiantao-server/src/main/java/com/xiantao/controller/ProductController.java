package com.xiantao.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiantao.common.Result;
import com.xiantao.dto.ProductDTO;
import com.xiantao.dto.ProductQueryDTO;
import com.xiantao.service.ProductService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.PageVO;
import com.xiantao.vo.ProductVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<PageVO<ProductVO>> getProductList(ProductQueryDTO query) {
        PageVO<ProductVO> page = productService.getProductList(query);
        return Result.success(page);
    }

    @GetMapping("/search")
    public Result<PageVO<ProductVO>> searchProducts(ProductQueryDTO query) {
        PageVO<ProductVO> page = productService.getProductList(query);
        return Result.success(page);
    }

    @GetMapping("/nearby")
    public Result<List<ProductVO>> getNearbyProducts(ProductQueryDTO query) {
        List<ProductVO> list = productService.getNearbyProducts(query);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<ProductVO> createProduct(HttpServletRequest request, @Valid @RequestBody ProductDTO dto) {
        Long userId = JwtUtils.getCurrentUserId(request);
        ProductVO vo = productService.createProduct(userId, dto);
        return Result.success("发布成功", vo);
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(HttpServletRequest request, @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        Long userId = JwtUtils.getCurrentUserId(request);
        ProductVO vo = productService.updateProduct(userId, id, dto);
        return Result.success("修改成功", vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        productService.deleteProduct(userId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/my")
    public Result<PageVO<ProductVO>> getMyProducts(HttpServletRequest request, ProductQueryDTO query) {
        Long userId = JwtUtils.getCurrentUserId(request);
        PageVO<ProductVO> page = productService.getMyProducts(userId, query);
        return Result.success(page);
    }
}
