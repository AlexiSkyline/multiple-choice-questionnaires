package org.skyline.mcq.infrastructure.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;
    private int status;
    private HttpStatus httpStatus;
    private String error;
    private String message;
    private String path;
}
