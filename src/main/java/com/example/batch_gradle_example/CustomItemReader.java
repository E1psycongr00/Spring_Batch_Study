package com.example.batch_gradle_example;


import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CustomItemReader implements ItemReader<String> {

    private int i = 0;

    @Override
    public String read()
        throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        i++;
        if (i == 3) {
            throw new SkippableException("this exception is skipped");
        }
        System.out.println("ItemReader : " + i);
        return i > 20 ? null : String.valueOf(i);
    }
}
