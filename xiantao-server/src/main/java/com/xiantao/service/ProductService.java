package com.xiantao.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.ProductDTO;
import com.xiantao.dto.ProductQueryDTO;
import com.xiantao.entity.Product;
import com.xiantao.vo.PageVO;
import com.xiantao.vo.ProductVO;

public interface ProductService extends IService<Product> {

    PageVO<ProductVO> getProductList(ProductQueryDTO query);

    ProductVO getProductDetail(Long id);

    ProductVO createProduct(Long userId, ProductDTO dto);

    ProductVO updateProduct(Long userId, Long id, ProductDTO dto);

    void deleteProduct(Long userId, Long id);

    PageVO<ProductVO> getMyProducts(Long userId, ProductQueryDTO query);

    Page<ProductVO> getAdminProductList(Integer pageNum, Integer pageSize, Long categoryId, Integer status,
            String keyword);

    ProductVO adminCreateProduct(ProductDTO dto);

    ProductVO adminUpdateProduct(Long id, ProductDTO dto);

    void adminDeleteProduct(Long id);

    void adminUpdateStatus(Long id, Integer status);

    List<ProductVO> getNearbyProducts(ProductQueryDTO query);
}
