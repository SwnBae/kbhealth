package kb.health.exception;

/**
 * 알림 관련 예외 처리 클래스
 */
public class NotificationException extends GetRuntimeException {

    public NotificationException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    /**
     * 알림을 찾을 수 없는 경우 발생하는 예외
     * @return NotificationException
     */
    public static NotificationException notificationNotFound() {
        return new NotificationException(ExceptionCode.NOTIFICATION_NOT_FOUND);
    }

    /**
     * 본인의 알림이 아닌 경우 접근 권한이 없을 때 발생하는 예외
     * @return NotificationException
     */
    public static NotificationException unauthorizedAccess() {
        return new NotificationException(ExceptionCode.UNAUTHORIZED_NOTIFICATION_ACCESS);
    }
}