package com.xiantao.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private String icon;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;
}
