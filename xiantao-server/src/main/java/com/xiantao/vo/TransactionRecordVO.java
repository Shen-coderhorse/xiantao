package com.xiantao.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionRecordVO {

    private Long id;

    private Long orderId;

    private String orderNo;

    private Integer transactionType;

    private String transactionTypeText;

    private BigDecimal amount;

    private Long fromUserId;

    private String fromUserName;

    private Long toUserId;

    private String toUserName;

    private Integer status;

    private String statusText;

    private String remark;

    private LocalDateTime createTime;
}
