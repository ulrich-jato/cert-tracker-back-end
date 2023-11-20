package com.devops.certtracker.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String email;
}
