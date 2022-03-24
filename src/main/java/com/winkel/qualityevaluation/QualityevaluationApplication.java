package com.winkel.qualityevaluation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@MapperScan(basePackages = {"com.winkel.qualityevaluation.dao"})
@ComponentScan(basePackages = {"com.winkel.qualityevaluation.dao","com.winkel.qualityevaluation.service", "com.winkel.qualityevaluation.controller","com.winkel.qualityevaluation.util", "com.winkel.qualityevaluation.config","com.winkel.qualityevaluation.security"})
public class QualityevaluationApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualityevaluationApplication.class, args);
    }

}
