package com.xiantao.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {

    private Integer type;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
