package com.churchsoft.global.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String messge){
        super(messge);
    }
}
