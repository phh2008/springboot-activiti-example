package com.phh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration;

/**
 * 描述
 *
 * @author phh
 * @version V1.0
 * @date 2019/10/17
 */
@MapperScan("com.phh.dao")
@SpringBootApplication(exclude = {SpringBootWebSecurityConfiguration.class})
public class ActivitiExampleApp {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiExampleApp.class, args);
    }
}
