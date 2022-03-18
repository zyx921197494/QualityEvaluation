package com.winkel.qualityevaluation.config;
/*
  @ClassName MybatisPlusConfig
  @Description
  @Author winkel
  @Date 2022-03-17 14:30
  */

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType("mysql");
        paginationInterceptor.setOverflow(true);
        return paginationInterceptor;
    }
}
