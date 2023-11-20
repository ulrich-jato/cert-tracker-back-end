package com.devops.certtracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignoutResponse {
    private String jwtCookie;
    private String jwtRefreshCookie;
    private MessageResponse messageResponse;
}
