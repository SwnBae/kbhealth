package kb.health.Exception;

/**
 * 코드 추가 예정
 */
public class FollowException extends RuntimeException{
    public int code;

    public FollowException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.code = exceptionCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public static FollowException cannotFollowYourself() {
        return new FollowException("자기 자신은 팔로우 할 수 없습니다.", ExceptionCode.CANNOT_FOLLOW_YOURSELF);
    }

    public static FollowException followNotFound() {
        return new FollowException("해당 관계가 존재하지 않습니다.", ExceptionCode.FOLLOW_NOT_FOUND);
    }
}
