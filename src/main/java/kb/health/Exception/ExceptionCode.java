package kb.health.Exception;

public enum ExceptionCode {

    // ==== Member 관련 ====
    DUPLICATE_PHONE_NUMBER(1001),
    DUPLICATE_USERNAME(1002),
    DUPLICATE_ACCOUNT(1003),
    MEMBER_NOT_FOUND_BY_PHONE(1004),
    MEMBER_NOT_FOUND_BY_USERNAME(1005),
    MEMBER_NOT_FOUND_BY_ACCOUNT(1006),

    // ==== Follow 관련 ====
    CANNOT_FOLLOW_YOURSELF(2001),
    FOLLOW_NOT_FOUND(2002);

    private final int code;

    ExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
