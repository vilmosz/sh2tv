package tv.shapeshifting.controllers;

import java.io.IOException;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractController {
        
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<RestErrorMessage> handleIllegalArgumentException(final IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new RestErrorMessage("Illegal argument", e.getMessage()));
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleResourceNotFoundException(final ResourceNotFoundException e) {
        return ResponseEntity.badRequest().body(new RestErrorMessage("Resource not found", e.getMessage()));
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<RestErrorMessage> handleIOException(final IOException e) {
        return ResponseEntity.badRequest().body(new RestErrorMessage("IO Exception", e.getMessage()));
    }
    
}
