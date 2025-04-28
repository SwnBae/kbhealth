package kb.health.Exception;

public class LoginNeededException extends RuntimeException {
    public int code;
    public LoginNeededException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.code = exceptionCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public static LoginNeededException loginProcessNeeded() {
        return new LoginNeededException("로그인이 필요합니다.", ExceptionCode.LOGIN_PROCESS_NEEDED);
    }

}
