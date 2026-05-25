package com.xiantao.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

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

    private Long addressId;

    private String receiverName;

    private String receiverPhone;

    private String address;

    private Integer status;

    private String statusText;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private LocalDateTime completeTime;
}
