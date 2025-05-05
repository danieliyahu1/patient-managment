package com.pm.auth_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class LoginResponseDTO {
    private final String token;
}
