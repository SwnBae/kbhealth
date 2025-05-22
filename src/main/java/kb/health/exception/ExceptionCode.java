package kb.health.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // ==== Member 관련 ====
    DUPLICATE_PHONE_NUMBER(1001, "이미 존재하는 휴대폰 번호입니다.", HttpStatus.BAD_REQUEST, null),
    DUPLICATE_USERNAME(1002, "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST, null),
    DUPLICATE_ACCOUNT(1003, "이미 존재하는 계정입니다.", HttpStatus.BAD_REQUEST, null),
    MEMBER_NOT_FOUND_BY_PHONE(1004, "해당 번호로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND, "/login"),
    MEMBER_NOT_FOUND_BY_USERNAME(1005, "해당 닉네임으로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND, "/login"),
    MEMBER_NOT_FOUND_BY_ACCOUNT(1006, "해당 계정으로 등록된 회원이 없습니다.", HttpStatus.NOT_FOUND, "/login"),

    // ==== Follow 관련 ====
    CANNOT_FOLLOW_YOURSELF(2001, "자기 자신을 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST, null),
    FOLLOW_NOT_FOUND(2002, "팔로우 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND, null),

    // ==== Login Verify 관련 ====
    LOGIN_PROCESS_NEEDED(3001, "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED, "/login"),
    CANNOT_FIND_MEMBER(3002, "회원 정보를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED, "/login"),
    INVALID_PASSWORD(3003, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED, "/login"),
    INVALID_TOKEN(3004, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED, "/login"),

    // ==== Feed 관련 ====
    CANNOT_FIND_FEED(4001, "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "/feed"),
    CANNOT_FIND_COMMENT(4002, "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "/feed"),
    UNAUTHORIZED_ACCESS_TO_RESOURCE(4003, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN, "/"),

    // ==== File 관련 ====
    Image_Upload_Failed(4005, "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null),

    // ==== Notification 관련 ====
    NOTIFICATION_NOT_FOUND(5001, "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, null),
    UNAUTHORIZED_NOTIFICATION_ACCESS(5002, "본인의 알림만 접근할 수 있습니다.", HttpStatus.FORBIDDEN, null);

    private final int code;
    private final String message;
    private final HttpStatus status;
    private final String redirectPath; // ✅ 추가

    ExceptionCode(int code, String message, HttpStatus status, String redirectPath) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.redirectPath = redirectPath;
    }
}
