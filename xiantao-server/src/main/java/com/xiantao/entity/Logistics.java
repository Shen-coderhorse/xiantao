package com.xiantao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("logistics")
public class Logistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String companyCode;

    private String companyName;

    private String trackingNo;

    private Integer status;

    private String currentLocation;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
