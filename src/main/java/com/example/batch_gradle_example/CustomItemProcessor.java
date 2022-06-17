package com.example.batch_gradle_example;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {
    
    @Override public Customer process(Customer item) throws Exception {
        item.setName(item.getName().toUpperCase());
        return item;
        
    }
}
