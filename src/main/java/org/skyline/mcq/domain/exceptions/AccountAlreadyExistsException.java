package org.skyline.mcq.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException() {
        super("Email or username is already registered");
    }

    public AccountAlreadyExistsException(String fieldName) {
        super(fieldName + " is already registered");
    }
}
