package kb.health.Exception;

import kb.health.domain.Member;

/**
 * 코드 추가 예정
 */
public class MemberException extends RuntimeException{
    public int code;

    public MemberException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.code = exceptionCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public static MemberException duplicatePhoneNumber() {
        return new MemberException("해당 휴대폰 번호는 이미 존재합니다.", ExceptionCode.DUPLICATE_PHONE_NUMBER);
    }

    public static MemberException duplicateUserName() {
        return new MemberException("해당 닉네임은 이미 존재합니다.", ExceptionCode.DUPLICATE_USERNAME);
    }

    public static MemberException duplicateAccount() {
        return new MemberException("해당 계정은 이미 존재합니다.", ExceptionCode.DUPLICATE_ACCOUNT);
    }

    public static MemberException memberNotFoundByPhoneNumber() {
        return new MemberException("해당 번호로 등록된 회원이 없습니다.", ExceptionCode.MEMBER_NOT_FOUND_BY_PHONE);
    }

    public static MemberException memberNotFoundByUserName() {
        return new MemberException("해당 닉네임으로 등록된 회원이 없습니다.", ExceptionCode.MEMBER_NOT_FOUND_BY_USERNAME);
    }

    public static MemberException memberNotFoundByAccount() {
        return new MemberException("해당 계정으로 등록된 회원이 없습니다.", ExceptionCode.MEMBER_NOT_FOUND_BY_ACCOUNT);
    }

    public static MemberException invalidPassword(){
        return new MemberException("비밀번호가 일치하지 않습니다.", ExceptionCode.INVALID_PASSWORD);
    }

    //아마 사용되지 않을 메서드
//    public static MemberException memberNotFoundById() {
//        throw new MemberException("해당 ID(FK)의 회원이 존재하지 않습니다: ");
//    }
}
