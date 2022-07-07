package com.example.batch_gradle_example;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class FaultTolerantConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(5)
            .reader(itemReader())
            .processor(itemProcess())
            .writer(itemWriter())
            .faultTolerant()
//                .skip(SkippableException.class)
//                .skipLimit(4)
            .skipPolicy(limitCheckingItemSkipPolicy())
            .build();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(SkippableException.class,
            true); // true일 경우 skip, false일 경우 skip을 실행하지 않음

        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(3,
            exceptionClass);
        return limitCheckingItemSkipPolicy;
    }

    public ItemReader<String> itemReader() {
        return new CustomItemReader();
    }

    @Bean
    public ItemWriter<? super String> itemWriter() {
        return new SkipItemWriter();
    }


    @Bean
    public ItemProcessor<String, String> itemProcess() {
        return new SkipItemProcessor();
    }
}
