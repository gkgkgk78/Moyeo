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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;

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
                .reader(this.itemReader())
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
    public JpaPagingItemReader<Post> itemReader() {
        return new JpaPagingItemReaderBuilder<Post>()
                .queryString("SELECT p FROM Post p")
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
