package com.example.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final BatchService batchService;
    /* 매일 오전 11시, 오후 5시에 스케줄링*/
    @Scheduled(cron = "0 0 11,17 * * *",zone = "Asia/Seoul")
    public void runJobAtEleven() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        long nano = System.currentTimeMillis();
        Date now = new Date(System.currentTimeMillis());
        log.info("Schedule start");
        JobParameters params = new JobParametersBuilder()
                .addString("start", UUID.randomUUID()+"")
                .addString("end", UUID.randomUUID()+"")
                .toJobParameters();
        jobLauncher.run(batchService.job(),
                params);
    }

}
