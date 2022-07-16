package com.onlinemuseum.controller;

import com.onlinemuseum.response.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

@Component
public class ResponseCreator {
    public ResponseEntity<CustomResponse> createResponseEntityWithStatus(CustomResponse customResponse) {
        HttpStatus status;
        if (customResponse.getFailureMessages() == null) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(customResponse, status);
    }
}
