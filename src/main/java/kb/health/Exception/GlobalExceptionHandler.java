package kb.health.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> loginException(LoginException e) {
        switch (e.getExceptionCode()) {
            case LOGIN_PROCESS_NEEDED:
            case CANNOT_FIND_MEMBER:
            case INVALID_PASSWORD:
            case INVALID_TOKEN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage(), "redirect" , "/login"));
        }
        return null;
    }
}

