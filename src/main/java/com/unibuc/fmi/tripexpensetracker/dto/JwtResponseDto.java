package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;

    public JwtResponseDto(String accessToken, Long id, String username, String email) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
