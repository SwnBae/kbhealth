package kb.health.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // ==== Member 관련 ====
    DUPLICATE_PHONE_NUMBER(1001, "이미 존재하는 휴대폰 번호입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME(1002, "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ACCOUNT(1003, "이미 존재하는 계정입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND_BY_PHONE(1004, "해당 번호로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND_BY_USERNAME(1005, "해당 닉네임으로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND_BY_ACCOUNT(1006, "해당 계정으로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND),


    // ==== Follow 관련 ====
    CANNOT_FOLLOW_YOURSELF(2001, "자기 자신을 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_NOT_FOUND(2002, "팔로우 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // ==== Login Verify 관련 ====
    LOGIN_PROCESS_NEEDED(3001, "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    CANNOT_FIND_MEMBER(3002, "회원 정보를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(3003, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(3004, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus status;

    ExceptionCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
