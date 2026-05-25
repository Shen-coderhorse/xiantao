package com.xiantao.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class LogisticsVO {

    private Long id;

    private Long orderId;

    private String companyCode;

    private String companyName;

    private String trackingNo;

    private Integer status;

    private String statusText;

    private String currentLocation;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private List<LogisticsTrackVO> tracks;
}
