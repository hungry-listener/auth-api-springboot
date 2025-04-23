package com.ekagra.auth.exceptions;

public class UsernameEmptyException extends RuntimeException{
    public UsernameEmptyException(String message){
        super(message);
    }
}
