package com.xiantao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.dto.ProductDTO;
import com.xiantao.dto.ProductQueryDTO;
import com.xiantao.entity.Category;
import com.xiantao.entity.Product;
import com.xiantao.entity.User;
import com.xiantao.mapper.ProductMapper;
import com.xiantao.service.CategoryService;
import com.xiantao.service.ProductService;
import com.xiantao.service.UserService;
import com.xiantao.vo.PageVO;
import com.xiantao.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public PageVO<ProductVO> getProductList(ProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Product::getTitle, query.getKeyword());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        this.page(page, wrapper);

        List<ProductVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setViewCount(product.getViewCount() + 1);
        this.updateById(product);

        return convertToVO(product);
    }

    @Override
    public ProductVO createProduct(Long userId, ProductDTO dto) {
        Category category = categoryService.getById(dto.getCategoryId());
        if (category == null || category.getStatus() != 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setSellerId(userId);
        product.setStatus(1);
        product.setViewCount(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        this.save(product);

        return convertToVO(product);
    }

    @Override
    public ProductVO updateProduct(Long userId, Long id, ProductDTO dto) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权修改此商品");
        }

        if (product.getStatus() == 2) {
            throw new BusinessException("已售商品不能修改");
        }

        Category category = categoryService.getById(dto.getCategoryId());
        if (category == null || category.getStatus() != 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        BeanUtils.copyProperties(dto, product);
        product.setUpdateTime(LocalDateTime.now());
        this.updateById(product);

        return convertToVO(product);
    }

    @Override
    public void deleteProduct(Long userId, Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权删除此商品");
        }

        this.removeById(id);
    }

    @Override
    public PageVO<ProductVO> getMyProducts(Long userId, ProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId);

        if (query.getStatus() != null) {
            wrapper.eq(Product::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        this.page(page, wrapper);

        List<ProductVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public Page<ProductVO> getAdminProductList(Integer pageNum, Integer pageSize, Long categoryId, Integer status, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getTitle, keyword);
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(pageNum, pageSize);
        this.page(page, wrapper);

        Page<ProductVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public ProductVO adminCreateProduct(ProductDTO dto) {
        Category category = categoryService.getById(dto.getCategoryId());
        if (category == null || category.getStatus() != 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setSellerId(1L);
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        product.setViewCount(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        this.save(product);

        return convertToVO(product);
    }

    @Override
    public ProductVO adminUpdateProduct(Long id, ProductDTO dto) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        Category category = categoryService.getById(dto.getCategoryId());
        if (category == null || category.getStatus() != 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        BeanUtils.copyProperties(dto, product);
        product.setUpdateTime(LocalDateTime.now());
        this.updateById(product);

        return convertToVO(product);
    }

    @Override
    public void adminDeleteProduct(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        this.removeById(id);
    }

    @Override
    public void adminUpdateStatus(Long id, Integer status) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (status < 0 || status > 2) {
            throw new BusinessException("状态值不正确");
        }

        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        this.updateById(product);
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        if (StringUtils.hasText(product.getImages())) {
            vo.setImageList(Arrays.asList(product.getImages().split(",")));
        } else {
            vo.setImageList(Collections.emptyList());
        }

        User seller = userService.getById(product.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }

        Category category = categoryService.getById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        return vo;
    }
}
