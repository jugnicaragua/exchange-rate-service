package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.exception.InvalidRangeException;
import ni.org.jug.exchangerate.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author aalaniz
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRangeException.class)
    public ResponseEntity handleInvalidRangeException(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
