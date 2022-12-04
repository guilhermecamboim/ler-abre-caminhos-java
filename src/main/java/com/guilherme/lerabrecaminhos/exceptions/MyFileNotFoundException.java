package com.guilherme.lerabrecaminhos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException {
    public MyFileNotFoundException(String message) {
        super();
    }

    public MyFileNotFoundException(String message, Throwable cause) {
        super();
    }
}
