package com.example.batch_gradle_example;

import org.springframework.batch.item.*;

import java.util.List;

public class CustomItemStreamReader implements ItemStreamReader<String> {
    
    private final List<String> items;
    private int index = -1;
    private boolean restart = false;
    
    public CustomItemStreamReader(List<String> items) {
        this.items = items;
        this.index = 0;
    }
    
    @Override
    public String read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        
        String item = null;
        
        if (this.index < this.items.size()) {
            item = this.items.get(index);
            index++;
        }
        if (this.index == 6 && !restart) {
            throw new RuntimeException("Restart");
        }
        
        return item;
    }
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("index")) { // db에 index라는 키가 이미 포함되어 있다는 의미
            index = executionContext.getInt("index");
            this.restart = true;
        } else {
            index = 0;
            executionContext.put("index", index);
        }
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // chunk size마다 한 사이클이 완료 될때마다 업데이트 메서드 호출
        executionContext.put("index", index);
        
    }
    
    @Override
    public void close() throws ItemStreamException {
        // 예외 발생시 초기화 작업 해지
        System.out.println("close");
    }
}
