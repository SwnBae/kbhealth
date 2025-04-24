package kb.health.Exception;
/**
 * 코드 추가 예정
 */
public class MemberException extends RuntimeException{
    private int code;

    public MemberException(String message) {
        super(message);
    }

    public static MemberException duplicatePhoneNumber() {
        return new MemberException("해당 휴대폰 번호는 이미 존재합니다.");
    }

    public static MemberException duplicateUserName() {
        return new MemberException("해당 닉네임은 이미 존재합니다.");
    }

    public static MemberException memberNotFoundByPhoneNumber() {
        return new MemberException("해당 번호로 등록된 회원이 없습니다: ");
    }

    public static MemberException memberNotFoundByUserName() {
        return new MemberException("해당 닉네임으로 등록된 회원이 없습니다: ");
    }

    //아마 사용되지 않을 메서드
    public static MemberException memberNotFoundById() {
        throw new MemberException("해당 ID(FK)의 회원이 존재하지 않습니다: ");
    }
}
