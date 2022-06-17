package com.example.batch_gradle_example;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ChunkConfiguration {
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
                                .incrementer(new RunIdIncrementer())
                                .start(step1())
                                .next(step2())
                                .next(step3())
                                .build();
    }
    
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                                 .<String, String>chunk(2)
                                 .reader(new ListItemReader<>(
                                         Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")))
                                 .processor(new ItemProcessor<String, String>() {
                                     @Override public String process(String item) throws Exception {
                                         Thread.sleep(300);
                                         System.out.println(item);
                                         return "my_" + item;
                                     }
                                 })
                                 .writer(new ItemWriter<String>() {
                                     @Override public void write(List<? extends String> items) throws Exception {
                                         Thread.sleep(1000);
                                         System.out.println(items);
                                     }
                                 })
                                 .build();
    }
    
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .<Customer, Customer>chunk(3)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }
    
    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .<String, String>chunk(5)
                .reader(itemStreamReader())
                .writer(itemStreamWriter())
                .build();
    }
    
    @Bean
    public ItemProcessor<? super Customer, ? extends Customer> itemProcessor() {
        return new CustomItemProcessor();
    }
    
    @Bean
    public ItemWriter<? super Customer> itemWriter() {
        return new CustomItemWriter();
    }
    @Bean
    public ItemReader<Customer> itemReader() {
        return new CustomItemReader(Arrays.asList(new Customer("user1")
                , new Customer("user2")
                , new Customer("user3")));
    }
    
    @Bean
    public CustomItemStreamReader itemStreamReader() {
        List<String> items = new ArrayList<>(10);
        
        for (int i = 0; i < 10; ++i) {
            items.add(String.valueOf(i));
        }
        return new CustomItemStreamReader(items);
    }
    
    @Bean
    public CustomStreamItemWriter itemStreamWriter() {
        return new CustomStreamItemWriter();
    }
}