
package com.example.coffeeshop;

import com.example.coffeeshop.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        400,
                        "Bad Request",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException e) {

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        400,
                        "Bad Request",
                        e.getMessage()
                ));
    }
}
