package com.xiantao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "被评价人ID不能为空")
    private Long revieweeId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 3, message = "评分最高为3")
    private Integer rating;

    private String content;
}
