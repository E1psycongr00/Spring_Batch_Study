package com.example.batch_gradle_example;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class RepeatTemplateConfiguration {
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(step3())
                .next(step1())
                .next(step2())
                .build();
    }
    
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(new CustomItemReader())
                .processor(new ItemProcessor<String, String>() {
                    
                    RepeatTemplate repeatTemplate = new RepeatTemplate();
                    
                    @Override
                    public String process(String item) throws Exception {

                        CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
                        CompletionPolicy[] completionPolicies = new CompletionPolicy[] {
                                new SimpleCompletionPolicy(3), // 청크 횟수 제한만큰만 반복 허용
                                new TimeoutTerminationPolicy(3000) // 3초 이상 실행되는 경우 반복 종료
                        };

                        completionPolicy.setPolicies(completionPolicies);
                        repeatTemplate.setCompletionPolicy(completionPolicy);
                        
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
    
                                System.out.println("step1: repeatTemplate is testing");
                                return RepeatStatus.CONTINUABLE;
                            }
                        });
                        return item;
                    }
                })
                .writer(System.out::println)
                .build();
    }
    
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .<String, String>chunk(10)
                .reader(new CustomItemReader())
                .processor(new ItemProcessor<String, String>() {
                    
                    RepeatTemplate repeatTemplate = new RepeatTemplate();
                    @Override
                    public String process(String item) throws Exception {
                        
                        repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler());
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                System.out.println("step2: repeatTemplate is testing");
                                throw new RuntimeException("Exception is occurred");
                            }
                        });
                        
                        return item;
                    };
                })
                .writer(System.out::println)
                .build();
    }
    
    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .<String, String>chunk(10)
                .reader(new CustomItemReader())
                .processor(new ItemProcessor<String, String>() {
                    
                    RepeatTemplate repeatTemplate = new RepeatTemplate();
                    @Override
                    public String process(String item) throws Exception {
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                System.out.println("step3: repeatTemplate is testing");
                                return RepeatStatus.FINISHED;
                            }
                        });
                        
                        return item;
                    }
                })
                .writer(System.out::println)
                .build();
    }
    
    
    @Bean
    public ExceptionHandler simpleLimitExceptionHandler() {
        // 3번까지 예외 발생시 반복해서 process 수행. 3번을 초과하는 경우 예외를 던지고 종료
        return new SimpleLimitExceptionHandler(3);
    }
}
