package kb.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> loginException(LoginException e) {
        String message = e.getMessage();

        if (message == null) {
            message = "알 수 없는 오류가 발생했습니다."; // 기본 메시지 설정
        }

        switch (e.getExceptionCode()) {
            case LOGIN_PROCESS_NEEDED:
            case CANNOT_FIND_MEMBER:
            case INVALID_PASSWORD:
            case INVALID_TOKEN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", message, "redirect" , "/login"));
        }
        return null;
    }
}

