package com.moyeo.main.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private int CORE_POOL_SIZE = 3;//최초 동작시 corepoolisze만큼 스레드 생성
    private int MAX_POOL_SIZE = 10;//queue 사이즈 이상의 요청이 들어올시, 스레드의 개수를 maxpoolsize만큼 늘림
    private int QUEUE_CAPACITY = 100_000;//corepoolsize 이상의 요청이들어올시, inkedblockingqueue 에서 대기 사이즈 설정

    @Bean(name = "sampleExecutor")
    public Executor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setThreadNamePrefix("Executor-");//스레드명 설정을 의미를 함

        return taskExecutor;
    }


}