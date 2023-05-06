package com.example.batch;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    String message;
    public CustomException(String message){
        this.message = message;
    }

}
