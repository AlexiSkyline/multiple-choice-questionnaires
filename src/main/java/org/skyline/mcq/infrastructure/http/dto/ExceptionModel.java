package org.skyline.mcq.infrastructure.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public class ExceptionModel<T> {

    private final String timeStamp;
    private final int httpError;
    private final HttpStatusCode httpStatus;
    private T information;
}
