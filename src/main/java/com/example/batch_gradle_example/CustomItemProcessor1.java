package com.example.batch_gradle_example;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor1 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {
    
    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        System.out.println("Processor_1");
        
        return item;
    }
}
