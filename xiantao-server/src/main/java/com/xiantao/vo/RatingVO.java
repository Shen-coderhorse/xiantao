package com.xiantao.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RatingVO {

    private Long id;

    private Long orderId;

    private Long reviewerId;

    private String reviewerName;

    private String reviewerAvatar;

    private Long revieweeId;

    private String revieweeName;

    private String revieweeAvatar;

    private Integer rating;

    private String ratingText;

    private String content;

    private LocalDateTime createTime;
}
