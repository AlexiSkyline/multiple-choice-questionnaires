package org.skyline.mcq.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    private final String modelName;
    private final String identifier;
    private final String additionalInfo;

    public ConflictException(String modelName, String identifier, String additionalInfo) {
        this.modelName = modelName;
        this.identifier = identifier;
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String getMessage() {
        return String.format("%s with identifier '%s' caused a conflict. %s", modelName, identifier, additionalInfo);
    }
}