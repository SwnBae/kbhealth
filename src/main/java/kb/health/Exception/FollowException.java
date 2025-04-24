package kb.health.Exception;

public class FollowException extends RuntimeException{
    public FollowException(String message) {
        super(message);
    }

    public static FollowException cannotFollowYourself() {
        throw new FollowException("자기 자신은 팔로우 할 수 없습니다.");
    }

    //버튼으로 없는 관계는 취소 자체를 못하게 하므로, 실질적으로 사용하지 않을 예정
    public static FollowException followNotFound() {
        throw new FollowException("해당 관계가 존재하지 않습니다.");
    }
}
