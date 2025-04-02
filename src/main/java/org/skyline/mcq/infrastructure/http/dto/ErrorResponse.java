package org.skyline.mcq.infrastructure.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class ErrorResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private T message;
    private String path;
}
