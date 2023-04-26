package com.danim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;
//@EnableWebMvc
@ServletComponentScan
@EnableJpaAuditing
@SpringBootApplication
public class DanimApplication {
    // 서버 실행 전 아시아/서울 시간으로 서버 시간 동기화
    @PostConstruct
    public void started() {
        Locale.setDefault(Locale.KOREA);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DanimApplication.class, args);
    }

}
