package com.example.batch_gradle_example;

import org.springframework.batch.item.ItemProcessor;

public class SkipItemProcessor implements ItemProcessor<String, String> {

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {
        System.out.println("ItemProcessor : " + item);
        if (item.equals("6") || item.equals("7")) {
            throw new SkippableException("Process failed cnt : " + cnt);
        } else {
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }
}
