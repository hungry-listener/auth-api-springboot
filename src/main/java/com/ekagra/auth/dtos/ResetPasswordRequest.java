package com.ekagra.auth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String resetToken;
    private String newPassword;

}

