package kb.health.Exception;
import kb.health.domain.Member;
/**
 * 코드 추가 예정
 */
public class MemberException extends GetRuntimeException{

    public MemberException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static MemberException duplicatePhoneNumber() {
        return new MemberException(ExceptionCode.DUPLICATE_PHONE_NUMBER);
    }

    public static MemberException duplicateUserName() {
        return new MemberException(ExceptionCode.DUPLICATE_USERNAME);
    }

    public static MemberException duplicateAccount() {
        return new MemberException(ExceptionCode.DUPLICATE_ACCOUNT);
    }

    public static MemberException memberNotFoundByPhoneNumber() {
        return new MemberException(ExceptionCode.MEMBER_NOT_FOUND_BY_PHONE);
    }

    public static MemberException memberNotFoundByUserName() {
        return new MemberException(ExceptionCode.MEMBER_NOT_FOUND_BY_USERNAME);
    }

    public static MemberException memberNotFoundByAccount() {
        return new MemberException(ExceptionCode.MEMBER_NOT_FOUND_BY_ACCOUNT);
    }

    public static MemberException invalidPassword(){
        return new MemberException(ExceptionCode.INVALID_PASSWORD);
    }

    //아마 사용되지 않을 메서드
//    public static MemberException memberNotFoundById() {
//        throw new MemberException("해당 ID(FK)의 회원이 존재하지 않습니다: ");
//    }
}
