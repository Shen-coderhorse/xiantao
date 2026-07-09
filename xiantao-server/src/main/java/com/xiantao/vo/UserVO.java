package com.xiantao.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer status;

    private String role;

    private BigDecimal balance;

    private LocalDateTime createTime;

    private String token;
}
