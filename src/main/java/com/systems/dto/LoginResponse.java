package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserDTO user;
    
    public LoginResponse(String accessToken, String refreshToken, UserDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
        this.tokenType = "Bearer";
    }
}
