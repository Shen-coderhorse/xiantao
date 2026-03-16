package com.xiantao.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {

    private Long categoryId;

    private String keyword;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
