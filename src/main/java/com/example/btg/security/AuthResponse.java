package com.example.btg.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
}
