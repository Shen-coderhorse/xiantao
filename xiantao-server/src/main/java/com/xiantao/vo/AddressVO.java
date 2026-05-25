package com.xiantao.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AddressVO {

    private Long id;

    private Long userId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer isDefault;

    private String fullAddress;

    private LocalDateTime createTime;
}
