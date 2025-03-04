package org.skyline.mcq.infrastructure.http.dto;

import org.springframework.http.HttpStatus;

public record ResponseBody<T>(String timeStamp, int httpCode, HttpStatus httpStatus, String message, T data) {}