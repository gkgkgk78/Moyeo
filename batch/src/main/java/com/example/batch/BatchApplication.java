package com.example.batch;

import jdk.jfr.Enabled;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;

@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {
	@PostConstruct
	public void started() {
		Locale.setDefault(Locale.KOREA);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

}
