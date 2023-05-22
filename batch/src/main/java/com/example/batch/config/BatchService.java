package com.example.batch.config;

import com.example.batch.RestaurantRecommendDto.BatchStatistic;
import com.example.batch.RestaurantRecommendDto.PushTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.skip.SkipException;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchService {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private String JOB_NAME = "restaurantRecommendJob";
    private String STEP_NAME = "restaurantRecommendStep";
    private String CHUNK_NAME = "restaurantRecommendChunk";
    @Value("${sender}")
    private String sender;
    @Bean
    public Job job(){
        log.info("JOB 실행됨");
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(this.chunkStep())
                .build();
    }
    @Bean
    @JobScope
    public Step chunkStep() {
        return stepBuilderFactory.get(CHUNK_NAME)
                .<PushTable, BatchStatistic>chunk(3)
                .reader(this.itemReader())
                .processor(this.itemProcessor())
                .writer(this.itemWriter())
                .faultTolerant()
                .skip(SkipException.class)
                .skipLimit(100)
                .build();
    }
    @Bean
    @StepScope
    public ItemWriter<BatchStatistic> itemWriter() {
        JpaItemWriter<BatchStatistic> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    @Bean
    @StepScope
    public ItemProcessor<PushTable, BatchStatistic> itemProcessor() {
        return item -> {
            log.info("item : {}",item);
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .build();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ObjectMapper mapper = new ObjectMapper();
                Map<String,Object> map = Map.of(
                        "Title","pushdining",
                        "userId",item.getUserId(),
                        "deviceToken",item.getDeviceToken(),
                        "address1",item.getAddress1(),
                        "address2", item.getAddress2(),
                        "address3", item.getAddress3(),
                        "address4", item.getAddress4());
                HttpEntity<String> notificationEntity = new HttpEntity<String>(mapper.writeValueAsString(map),headers);
                ResponseEntity<String> res = restTemplate.exchange(sender,HttpMethod.POST,notificationEntity, String.class);

            return BatchStatistic.builder()
                    .deviceToken(item.getDeviceToken())
                    .address1(item.getAddress1())
                    .address2(item.getAddress2())
                    .address3(item.getAddress3())
                    .address4(item.getAddress4())
                    .build();
            }catch (RestClientException e){
                log.info("Skip-AutoGpt500");
                throw new SkipException("Skip 합니다.") {
                    @Override
                    public String getMessage() {
                        return super.getMessage();
                    }
                };
            }
        };
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<PushTable> itemReader() {
        log.info("start : {}","시작합니다");
        return new JpaPagingItemReaderBuilder<PushTable>()
                .queryString("SELECT P from PushTable P")
                .pageSize(3)
                .entityManagerFactory(entityManagerFactory)
                .name("PostReader")
                .build();
    }
}
