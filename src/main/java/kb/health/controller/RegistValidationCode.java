package kb.health.controller;


public enum RegistValidationCode {
    // 성공
    ACCOUNT_AVAILABLE("ACCOUNT_AVAILABLE"),
    USERNAME_AVAILABLE("USERNAME_AVAILABLE"),

    // 아이디 관련 오류
    ACCOUNT_EMPTY("ACCOUNT_EMPTY"),
    ACCOUNT_LENGTH_INVALID("ACCOUNT_LENGTH_INVALID"),
    ACCOUNT_DUPLICATE("ACCOUNT_DUPLICATE"),
    ACCOUNT_FORMAT_INVALID("ACCOUNT_FORMAT_INVALID"), // 영문+숫자만 허용 등

    // 닉네임 관련 오류
    USERNAME_EMPTY("USERNAME_EMPTY"),
    USERNAME_LENGTH_INVALID("USERNAME_LENGTH_INVALID"),
    USERNAME_DUPLICATE("USERNAME_DUPLICATE"),
    USERNAME_FORMAT_INVALID("USERNAME_FORMAT_INVALID"), // 특수문자 불가 등

    // 시스템 오류
    SERVER_ERROR("SERVER_ERROR"),
    VALIDATION_FAILED("VALIDATION_FAILED");

    private final String code;

    RegistValidationCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}