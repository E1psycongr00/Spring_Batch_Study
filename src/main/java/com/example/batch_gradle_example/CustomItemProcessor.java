package com.example.batch_gradle_example;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<String, String> {
    
    int cnt = 0;
    
    @Override
    public String process(String item) throws Exception {
        cnt++;
        
        return (item + cnt).toUpperCase();
    }
}
