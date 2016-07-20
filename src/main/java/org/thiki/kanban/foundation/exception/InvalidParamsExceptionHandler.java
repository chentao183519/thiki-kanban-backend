package org.thiki.kanban.foundation.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class InvalidParamsExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handle(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();
        IllegalArgumentException be = ex;
        body.put("message", be.getMessage());
        body.put("code", ExceptionCode.INVALID_PARAMS.code());

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        return handleExceptionInternal(ex, body, new HttpHeaders(), httpStatus, request);
    }
}
