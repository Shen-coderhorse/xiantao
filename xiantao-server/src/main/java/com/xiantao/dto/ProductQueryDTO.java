package com.xiantao.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductQueryDTO {

    private Long categoryId;

    private String keyword;

    private Integer status;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String sortBy;

    private String sortOrder;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Double distance;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
