package com.example.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final BatchConfig batchConfig;
//    @Scheduled(cron = "50 * * * * *")
    @Scheduled(cron = "* 30 * * * *",zone = "Asia/Seoul")
    public void runJobAtEleven() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        long nano = System.currentTimeMillis();
        log.info("Schedule start");
        JobParameters params = new JobParametersBuilder()
//                .addString("start", new SimpleDateFormat("yyyy-MM-dd ").format(nano)+"00:00:00")
//                .addString("end", new SimpleDateFormat("yyyy-MM-dd ").format(nano)+"11:00:00")
                .toJobParameters();
        log.info("jobparameter :{}",params);
        log.info("Job:{}",batchConfig.job());
        jobLauncher.run(batchConfig.job(),
                params);
    }

    @Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
    public void runJobAtFive() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        long nano = System.currentTimeMillis();
        JobParameters params = new JobParametersBuilder()
                .addString("startDate", new SimpleDateFormat("yyyy-MM-dd ").format(nano)+"13:00:00")
                .addString("endDate", new SimpleDateFormat("yyyy-MM-dd ").format(nano)+"17:00:00")
                .toJobParameters();
        jobLauncher.run(batchConfig.job(), params);
    }
}
