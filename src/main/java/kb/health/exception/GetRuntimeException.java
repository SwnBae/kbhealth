package kb.health.exception;

import lombok.Getter;

@Getter
public class GetRuntimeException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public GetRuntimeException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public int getCode() {
        return exceptionCode.getCode();
    }

    public String getExceptionMessage() {
        return exceptionCode.getMessage();
    }
}