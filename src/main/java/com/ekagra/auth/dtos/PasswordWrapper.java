package com.ekagra.auth.dtos;

public class PasswordWrapper {
    @PasswordPolicy
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

