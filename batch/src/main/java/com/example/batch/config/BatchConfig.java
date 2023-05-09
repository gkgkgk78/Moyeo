package com.example.batch.config;

import com.example.batch.RestTemplateResponseErrorHandler;
import com.example.batch.RestaurantRecommendDto.FirebaseCM;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private String JOB_NAME = "restaurantRecommendJob";
    private String STEP_NAME = "restaurantRecommendStep";
    private String CHUNK_NAME = "restaurantRecommendChunk";
    @Value("${flask}")
    private String autogpt;
    @Value("${notification}")
    private String noti;
    @Bean
    public Job job(){
        log.info("JOB 실행됨");
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(this.chunkStep())
//                .start()
//                .next(this.chunkStep())
//                .start(this.chunkStep())
                .build();
    }
    @Bean
    @JobScope
    public Step chunkStep() {
        return stepBuilderFactory.get(CHUNK_NAME)
                .<PushTable, FirebaseCM>chunk(3)
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
    public JpaItemWriter<FirebaseCM> itemWriter() {
        JpaItemWriter<FirebaseCM> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);

        return writer;
    }
    @Bean
    @StepScope
    public ItemProcessor<PushTable,FirebaseCM> itemProcessor() {
        return item -> {
            log.info("item : {}",item);
            String goal = "Search for a good restaurant near " + item.getAddress1() + " " + item.getAddress2() + " " + item.getAddress3()+" "+item.getAddress4() +".";

            RestTemplate restTemplate = new RestTemplateBuilder()
//                    .errorHandler(new RestTemplateResponseErrorHandler())
                    .build();

            // create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Title","restaurant");
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> map = new HashMap<>();
            map.put("data",goal);
            // create param

            HttpEntity<String> autoGptEntity = new HttpEntity<String>(mapper.writeValueAsString(map), headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(autogpt, HttpMethod.POST, autoGptEntity, String.class);

//            log.info("result:{}",response.getBody());

                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                mapper = new ObjectMapper();
                map = new HashMap<>();
                map.put("deviceToken",item.getDeviceToken());
                map.put("message",response.getBody());
                HttpEntity<String> notificationEntity = new HttpEntity<String>(mapper.writeValueAsString(map),headers);
                ResponseEntity<String> res = restTemplate.exchange(noti,HttpMethod.POST,notificationEntity, String.class);
                log.info("result :{}",res.getBody());
                return FirebaseCM.builder()
                        .id(item.getDeviceToken())
                        .message("푸시전송완료")
                        .build();
            }catch (RestClientException e){
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
//        log.info("end : {}",end);
//        Map<String,Object> parameter = new HashMap<>();
//        parameter.put("start",start);
//        parameter.put("end",end);
        return new JpaPagingItemReaderBuilder<PushTable>()
//                .queryString("SELECT t.postId, t.userId, t.address1, t.address2, t.address3, t.address4, t.deviceToken, t.createTime" +
//                        "FROM (" +
//                        "  SELECT p.postId, u.userId, p.address1, p.address2, p.address3, p.address4, u.deviceToken, p.createTime," +
//                        "    (SELECT COUNT(*) FROM Post p2 WHERE p2.user = p.user AND p2.createTime >= p.createTime) AS rn" +
//                        "  FROM Post p" +
//                        "  JOIN p.user u" +
//                        "  WHERE p.createTime BETWEEN :start AND :end AND p.postId = (" +
//                        "    SELECT MAX(p2.postId)" +
//                        "    FROM Post p2" +
//                        "    WHERE p2.user = p.user AND p2.createTime BETWEEN :start AND :end" +
//                        "  )" +
//                        "  " +
//                        "  UNION ALL" +
//                        "  " +
//                        "  SELECT mp.moyeoPostId, mm.userId, mp.address1, mp.address2, mp.address3, mp.address4, u.deviceToken, mp.createTime," +
//                        "    (SELECT COUNT(*) FROM MoyeoPost mp2 WHERE mp2.moyeoTimeline = mp.moyeoTimeline AND mp2.createTime >= mp.createTime) AS rn" +
//                        "  FROM MoyeoPost mp " +
//                        "  JOIN mp.moyeoTimeline mtl" +
//                        "  JOIN mtl.moyeoMembers mm" +
//                        "  JOIN mm.user u" +
//                        "  WHERE mp.createTime BETWEEN :start AND :end AND mp.moyeoPostId = (" +
//                        "    SELECT MAX(mp2.moyeoPostId)" +
//                        "    FROM MoyeoPost mp2" +
//                        "    WHERE mp2.moyeoTimeline = mp.moyeoTimeline AND mp2.createTime BETWEEN :start AND :end" +
//                        "  )" +
//                        ") t" +
//                        "WHERE t.rn = 1" +
//                        "ORDER BY t.createTime DESC")
                .queryString("SELECT P from PushTable P")
//                .queryString("SELECT MP FROM Moyeo_post MP inner join MP.moyeoTimeline MTL")
//                .parameterValues(parameter)
                .pageSize(3)
                .entityManagerFactory(entityManagerFactory)
                .name("PostReader")
                .build();
    }

    @Bean
    public Step taskStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("tasklet");
                        log.info("contribution : {}",contribution);
                        log.info("chunkContext : {}",chunkContext);
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }


}
