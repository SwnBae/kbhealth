package kb.health.exception;

import kb.health.controller.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ExceptionResponse> handleLoginException(LoginException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ExceptionResponse> handleMemberException(MemberException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(FollowException.class)
    public ResponseEntity<ExceptionResponse> handleFollowException(FollowException e) {
        return buildResponse(e.getExceptionCode());
    }

    @ExceptionHandler(FeedException.class)
    public ResponseEntity<ExceptionResponse> handleFeedException(FeedException e) {
        return buildResponse(e.getExceptionCode());
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<ExceptionResponse> buildResponse(ExceptionCode code) {
        ExceptionResponse response = new ExceptionResponse(
                code.getMessage(),
                code.getRedirectPath() // 필요 없으면 null
        );
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}
