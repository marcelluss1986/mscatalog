package com.mdss.mscatalog.resources.exceptions;

import com.mdss.mscatalog.dto.CustomErrorDTO;
import com.mdss.mscatalog.services.exceptions.DataException;
import com.mdss.mscatalog.services.exceptions.EmailException;
import com.mdss.mscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        StandardError err = new StandardError();
        HttpStatus status = HttpStatus.NOT_FOUND;
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(e.getMessage());
        err.setMessage("Resource Not found, try again!");
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity<StandardError> dataIntegrityExcption(DataException e, HttpServletRequest request){
        StandardError err = new StandardError();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(e.getMessage());
        err.setMessage("Violation Integrity!");
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> methodArgumentException(MethodArgumentNotValidException e, HttpServletRequest request){
        ValidationError err = new ValidationError();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(e.getMessage());
        err.setMessage("Validation error Exception!");
        err.setPath(request.getRequestURI());

        for (FieldError f: e.getBindingResult().getFieldErrors()) {
            err.addErrors(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<CustomErrorDTO> email(EmailException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
