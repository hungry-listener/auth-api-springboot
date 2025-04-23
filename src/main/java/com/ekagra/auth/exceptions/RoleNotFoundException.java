package com.ekagra.auth.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message){
        super(message);
    }
}
