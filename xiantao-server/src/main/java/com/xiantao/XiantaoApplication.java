package com.xiantao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiantao.mapper")
public class XiantaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiantaoApplication.class, args);
        System.out.println("========================================");
        System.out.println("   闲淘二手交易平台启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
