package com.example.security.utils;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ApiResponse {
    private boolean result;
    private Object data;
    private String message;
}
