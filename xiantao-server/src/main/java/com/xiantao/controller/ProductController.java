package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.dto.ProductDTO;
import com.xiantao.dto.ProductQueryDTO;
import com.xiantao.service.ProductService;
import com.xiantao.vo.PageVO;
import com.xiantao.vo.ProductVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<ProductVO> createProduct(HttpServletRequest request, @Valid @RequestBody ProductDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        ProductVO vo = productService.createProduct(userId, dto);
        return Result.success("发布成功", vo);
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        ProductVO vo = productService.updateProduct(userId, id, dto);
        return Result.success("修改成功", vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        productService.deleteProduct(userId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/my")
    public Result<PageVO<ProductVO>> getMyProducts(HttpServletRequest request, ProductQueryDTO query) {
        Long userId = (Long) request.getAttribute("userId");
        PageVO<ProductVO> page = productService.getMyProducts(userId, query);
        return Result.success(page);
    }
}
