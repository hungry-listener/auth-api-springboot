package com.ekagra.auth.exceptions;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message){
        super(message);
    }
}
