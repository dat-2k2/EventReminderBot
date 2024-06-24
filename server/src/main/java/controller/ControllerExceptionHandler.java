package controller;

import exception.EventNotFound;
import exception.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({UserNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handler(UserNotFound e) {
        return "User not found: "+e.getUserId();
    }

    @ExceptionHandler({EventNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handler(EventNotFound e) {
        return "Event not found: "+e.getEventId();
    }

    @ExceptionHandler({DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handler(DateTimeParseException e) {
        return "Date time not in the right format (required yyyy-MM-dd and/or HH::mm): "+e.getMessage();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handler(IllegalArgumentException e) {
        return "Request body not in the right format: "+e.getMessage();
    }

    @ExceptionHandler({NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handler(NumberFormatException e) {
        return "Need number format: "+e.getMessage();
    }
}
