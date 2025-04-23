package com.ekagra.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    
    private String email;

    private String username;

    private Long roleId;

    private Boolean isActive;

    private Boolean isApproved;

}