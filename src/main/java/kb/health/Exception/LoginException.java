package kb.health.Exception;

public class LoginException extends RuntimeException {
    public int code;
    public LoginException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.code = exceptionCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public static LoginException loginProcessNeeded() {
        return new LoginException("로그인이 필요합니다.", ExceptionCode.LOGIN_PROCESS_NEEDED);
    }

}
