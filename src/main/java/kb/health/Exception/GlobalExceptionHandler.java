package kb.health.Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> loginException(LoginException e) {
        switch (e.getExceptionCode()) {
            case LOGIN_PROCESS_NEEDED:
            case CANNOT_FIND_MEMBER:
            case INVALID_PASSWORD:
                break;
            case INVALID_TOKEN:
                break;

        }
    }
}

