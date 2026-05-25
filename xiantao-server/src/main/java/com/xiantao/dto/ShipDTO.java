package com.xiantao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipDTO {

    @NotNull(message = "物流公司编码不能为空")
    private String companyCode;

    @NotBlank(message = "物流公司名称不能为空")
    private String companyName;

    @NotBlank(message = "物流单号不能为空")
    private String trackingNo;
}
