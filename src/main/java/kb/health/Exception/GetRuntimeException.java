package kb.health.Exception;

import lombok.Getter;

@Getter
public class GetRuntimeException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public GetRuntimeException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public int getCode() {
        return exceptionCode.getCode();
    }

    public String getExceptionMessage() {
        return exceptionCode.getMessage();
    }
}