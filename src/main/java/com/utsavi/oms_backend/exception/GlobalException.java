package com.utsavi.oms_backend.exception;

import com.utsavi.oms_backend.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException nullPointerException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Null Pointer Exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Illegal Argument Exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException resourceAlreadyExistsException) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Resource Already Exists Exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException message) {
        if (message.getMessage().contains("unique_email_phone")) {
            throw new ResourceAlreadyExistsException("A user with this email and phone already exists.");
        } else if (message.getMessage().contains("unique_address")) {
            throw new ResourceAlreadyExistsException("This address already exists.");
        } else if (message.getMessage().contains("unique_name")) {
            throw new ResourceAlreadyExistsException("A product with this name already exists.");
        } else if (message.getMessage().contains("unique_user_address_per_order")) {
            throw new ResourceAlreadyExistsException("An order for this user and address already exists.");
        } else if (message.getMessage().contains("unique_order_product")) {
            throw new ResourceAlreadyExistsException("This product is already included in the order.");
        } else {
            throw new ResourceAlreadyExistsException("A duplicate entry was detected.");
        }
    }
}
