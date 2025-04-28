package kb.health.Exception;

public class LoginException extends GetRuntimeException {
    public LoginException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static LoginException loginProcessNeeded() {
        return new LoginException(ExceptionCode.LOGIN_PROCESS_NEEDED);
    }

    public static LoginException canNotFindMember() {
        return new LoginException(ExceptionCode.CANNOT_FIND_MEMBER);
    }

    public static LoginException invalidPassword() {
        return new LoginException(ExceptionCode.INVALID_PASSWORD);
    }
    
    public static LoginException inValidToken() {
        return new LoginException(ExceptionCode.INVALID_TOKEN);
    }
}
