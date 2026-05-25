package com.xiantao.vo;

import lombok.Data;

@Data
public class UserCreditVO {

    private Long userId;

    private Integer creditScore;

    private String creditLevel;

    private String creditLevelColor;

    private Integer totalTransactions;

    private Integer completedTransactions;

    private Integer cancelledTransactions;

    private Integer goodRatingCount;

    private Integer mediumRatingCount;

    private Integer badRatingCount;

    private Integer violationCount;
}
