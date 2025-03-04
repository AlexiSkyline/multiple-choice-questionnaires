package org.skyline.mcq.infrastructure.http;

import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ResponseHandler {

    public <T> ResponseEntity<ResponseBody<T>> responseBuild(HttpStatus httpStatus, String message, T data) {
        ResponseBody<T> responseBody = new ResponseBody<>( new Date().toString(), httpStatus.value(), httpStatus, message, data);
        return new ResponseEntity<>(responseBody, httpStatus);
    }
}
