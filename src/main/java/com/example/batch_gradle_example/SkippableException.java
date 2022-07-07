package com.example.batch_gradle_example;

public class SkippableException extends Exception {

    public SkippableException(String s) {
        super(s);
    }

    public SkippableException() {
        super();
    }
}
