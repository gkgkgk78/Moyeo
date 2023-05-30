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
/* 컨트롤러에서 스케줄링 설정 하는 클래스
*  블로그 참조 : https://devlog-jul95.tistory.com/9
*  */
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final BatchService batchService;
    /* 매일 오전 11시, 오후 5시에 스케줄링*/
    @Scheduled(cron = "0 0 11,17 * * *",zone = "Asia/Seoul")
    public void runJobAtEleven() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        log.info("Schedule start");
        /* Job을 실행 시키기 위해 Joblauncher에 jobparameters가 제공되어야 함
        * Jobparameter는 똑같은 값이 들어가면 안된다.
        * 그래서 randeomUUID 값을 파라메터로 전달*/
        JobParameters params = new JobParametersBuilder()
                .addString("start", UUID.randomUUID()+"")
                .addString("end", UUID.randomUUID()+"")
                .toJobParameters();
        jobLauncher.run(batchService.job(),
                params);
    }

}
