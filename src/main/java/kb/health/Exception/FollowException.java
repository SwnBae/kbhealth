package kb.health.Exception;

/**
 * 코드 추가 예정
 */
public class FollowException extends GetRuntimeException{
    public FollowException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static FollowException cannotFollowYourself() {
        return new FollowException(ExceptionCode.CANNOT_FOLLOW_YOURSELF);
    }

    public static FollowException followNotFound() {
        return new FollowException(ExceptionCode.FOLLOW_NOT_FOUND);
    }
}
