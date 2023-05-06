package com.example.batch.config;

import com.example.batch.RestaurantRecommendDto.FirebaseCM;
import com.example.batch.RestaurantRecommendDto.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .<Post, FirebaseCM>chunk(3)
                .reader(this.itemReader(null,null))
                .processor(this.itemProcessor())
                .writer(this.itemWriter())
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
    public ItemProcessor<Post,FirebaseCM> itemProcessor() {
        return item -> {
            log.info("item : {}",item);
            return FirebaseCM.builder()
                    .message("안녕").build();
        };
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Post> itemReader(@Value("#{jobParameters['start']}") String start,@Value("#{jobParameters['end']}") String end) {
        log.info("start : {}",start);
        log.info("end : {}",end);
        Map<String,Object> parameter = new HashMap<>();
        parameter.put("start",start);
        parameter.put("end",end);
        return new JpaPagingItemReaderBuilder<Post>()
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
//                .queryString("SELECT P from Post P inner join P.user U")
                .queryString("SELECT MP FROM Moyeo_post MP inner join MP.moyeoTimeline MTL")
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
