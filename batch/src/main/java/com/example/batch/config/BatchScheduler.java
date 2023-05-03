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

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private JobLauncher jobLauncher;
    private BatchConfig batchConfig;
    @Scheduled(cron = "0 0 11 * * ?")
    public void runJobAtEleven() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters params = new JobParametersBuilder()
                .addDate("date", new Date())
                .addString("query", "query for 11AM")
                .toJobParameters();
        jobLauncher.run(batchConfig.job(), params);
    }

    @Scheduled(cron = "0 0 17 * * ?")
    public void runJobAtFive() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters params = new JobParametersBuilder()
                .addDate("date", new Date())
                .addString("query", "query for 5PM")
                .toJobParameters();
        jobLauncher.run(batchConfig.job(), params);
    }
}
