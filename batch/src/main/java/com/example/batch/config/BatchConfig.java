package com.example.batch.config;

import com.example.batch.RestRecoTasklet;
import com.example.batch.RestaurantRecommendDto.FirebaseCM;
import com.example.batch.RestaurantRecommendDto.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private String JOB_NAME = "restaurantRecommendJob";
    private String STEP_NAME = "restaurantRecommendStep";
    private String CHUNK_NAME = "restaurantRecommendChunk";
    @Bean
    public Job job(){
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(this.taskStep())
                .next(this.chunkStep())
                .build();
    }
    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get(CHUNK_NAME)
                .<Post, FirebaseCM>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }
    @Bean
    public ItemWriter<FirebaseCM> itemWriter() {
        return new ItemWriter<FirebaseCM>() {
            @Override
            public void write(List<? extends FirebaseCM> items) throws Exception {

            }
        };
    }
    @Bean
    public ItemProcessor<Post,FirebaseCM> itemProcessor() {
        return new ItemProcessor<Post, FirebaseCM>() {
            @Override
            public FirebaseCM process(Post item) throws Exception {
                return null;
            }
        };
    }

    @Bean
    public ItemReader<Post> itemReader() {
        return new ItemReader<Post>() {
            @Override
            public Post read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                return null;
            }
        };
    }

    @Bean
    public Step taskStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .tasklet(new RestRecoTasklet())
                .build();
    }


}
