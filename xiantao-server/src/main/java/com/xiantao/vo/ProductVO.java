package com.xiantao.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Long categoryId;

    private String categoryName;

    private Long sellerId;

    private String sellerName;

    private String sellerAvatar;

    private List<String> imageList;

    private Integer status;

    private Integer viewCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
