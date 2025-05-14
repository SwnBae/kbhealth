package kb.health.exception;

public class ImageException extends GetRuntimeException {
    public ImageException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static ImageException imageUploadFail() {
        return new ImageException(ExceptionCode.Image_Upload_Failed);
    }
}
