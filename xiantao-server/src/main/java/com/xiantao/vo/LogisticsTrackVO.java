package com.xiantao.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LogisticsTrackVO {

    private Long id;

    private String location;

    private String description;

    private LocalDateTime trackTime;
}
