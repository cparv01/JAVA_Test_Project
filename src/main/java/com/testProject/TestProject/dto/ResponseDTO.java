package com.testProject.TestProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDTO {
    private String message;
    private boolean success;
    private Object data;

    public ResponseDTO(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

}
