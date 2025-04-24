package kb.health.Exception;

/**
 * 코드 추가 예정
 */
public class FollowException extends RuntimeException{
    private int code;

    public FollowException(String message) {
        super(message);
    }

    public static FollowException cannotFollowYourself() {
        return new FollowException("자기 자신은 팔로우 할 수 없습니다.");
    }

    //버튼으로 없는 관계는 취소 자체를 못하게 하므로, 실질적으로 사용하지 않을 예정
    public static FollowException followNotFound() {
        return new FollowException("해당 관계가 존재하지 않습니다.");
    }
}
