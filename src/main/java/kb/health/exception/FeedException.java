package kb.health.exception;

public class FeedException extends GetRuntimeException {
    public FeedException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static FeedException canNotFindPost() {
        return new FeedException(ExceptionCode.CANNOT_FIND_FEED);
    }

    public static FeedException canNotFindComment() {
        return new FeedException(ExceptionCode.CANNOT_FIND_COMMENT);
    }

    public static FeedException unauthorizeAccess() {
        return new FeedException(ExceptionCode.UNAUTHORIZED_ACCESS_TO_RESOURCE);
    }
}
