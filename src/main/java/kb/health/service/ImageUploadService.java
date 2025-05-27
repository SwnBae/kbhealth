package kb.health.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final String UPLOAD_PATH = "images";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB (application.yml과 동일)
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * ✅ 이미지 업로드 처리 (메모리 최적화 + 상세 로깅)
     */
    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }


        // ✅ 파일 크기 검증
        if (image.getSize() > MAX_FILE_SIZE) {
            String errorMsg = String.format("파일 크기가 너무 큽니다. 현재: %s, 최대: %s",
                    formatFileSize(image.getSize()), formatFileSize(MAX_FILE_SIZE));
            throw new IllegalArgumentException(errorMsg);
        }

        // ✅ 파일 확장자 검증
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !isAllowedExtension(originalFilename)) {
            String errorMsg = "지원하지 않는 파일 형식입니다. JPG, PNG, GIF, WEBP만 업로드 가능합니다.";
            throw new IllegalArgumentException(errorMsg);
        }

        try {
            // ✅ 고유한 파일명 생성
            String imageName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = Paths.get(UPLOAD_PATH, imageName);

            // ✅ 디렉터리 생성
            Files.createDirectories(filePath.getParent());

            // ✅ 파일 저장 시작 시간 측정
            long startTime = System.currentTimeMillis();

            // ✅ 파일 저장 (스트림 처리로 메모리 효율성 향상)
            try (var inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath);
            }

            // ✅ 저장 완료 로깅
            long endTime = System.currentTimeMillis();

            // ✅ URL 반환
            String resultUrl = String.format("/images/%s", imageName);
            return resultUrl;

        } catch (IOException e) {
            String errorMsg = "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage();
            e.printStackTrace();
            throw new IOException(errorMsg, e);
        }
    }

    /**
     * ✅ 파일 크기를 읽기 쉬운 형태로 변환
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] units = {"B", "KB", "MB", "GB"};
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), units[exp]);
    }

    /**
     * ✅ 여러 이미지 동시 업로드 (배치 처리)
     */
    public String[] uploadImages(MultipartFile[] images) throws IOException {
        if (images == null || images.length == 0) {
            return new String[0];
        }

        // ✅ 동시 업로드 개수 제한
        if (images.length > 5) {
            throw new IllegalArgumentException("한 번에 최대 5개의 이미지만 업로드 가능합니다.");
        }

        String[] imageUrls = new String[images.length];

        for (int i = 0; i < images.length; i++) {
            imageUrls[i] = uploadImage(images[i]);
        }

        return imageUrls;
    }

    /**
     * ✅ 이미지 삭제 (디스크 정리)
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/images/")) {
            return false;
        }

        try {
            String fileName = imageUrl.substring("/images/".length());
            Path filePath = Paths.get(UPLOAD_PATH, fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }

            return false;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * ✅ 파일 확장자 검증
     */
    private boolean isAllowedExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (lowerCaseFilename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}