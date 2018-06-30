package com.newshacker.web;

import com.newshacker.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({PostCreateRequestNotValidException.class})
    protected ResponseEntity<Object> handlePostCreateRequestNotValidException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({PostCreateErrorException.class})
    protected ResponseEntity<Object> handlePostCreateErrorException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({PostUpdateRequestNotAuthorizedException.class})
    protected ResponseEntity<Object> handlePostUpdateRequestNotAuthorizedException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({PostUpdateRequestNotValidException.class})
    protected ResponseEntity<Object> handlePostUpdateRequestNotValidException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({PostUpdateRequestPostNotExistsException.class})
    protected ResponseEntity<Object> handlePostUpdateRequestPostNotExistsException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({PostUpdateErrorException.class})
    protected ResponseEntity<Object> handlePostUpdateErrorException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorResponse(e.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
