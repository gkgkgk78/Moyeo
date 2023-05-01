package com.moyeo.main;

import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
	// 서버 실행 전 아시아/서울 시간으로 서버 시간 동기화
	@PostConstruct
	public void started() {
		Locale.setDefault(Locale.KOREA);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
