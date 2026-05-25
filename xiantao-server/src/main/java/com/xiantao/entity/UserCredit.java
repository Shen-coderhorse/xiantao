package com.xiantao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_credit")
public class UserCredit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer creditScore;

    private Integer totalTransactions;

    private Integer completedTransactions;

    private Integer cancelledTransactions;

    private Integer goodRatingCount;

    private Integer mediumRatingCount;

    private Integer badRatingCount;

    private Integer violationCount;

    private LocalDateTime updateTime;
}
