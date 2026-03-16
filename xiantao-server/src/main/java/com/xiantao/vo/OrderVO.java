package com.xiantao.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long productId;

    private String productTitle;

    private BigDecimal productPrice;

    private String productImage;

    private Long sellerId;

    private String sellerName;

    private String sellerAvatar;

    private Long buyerId;

    private String buyerName;

    private String buyerAvatar;

    private Integer status;

    private String statusText;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private LocalDateTime completeTime;
}
