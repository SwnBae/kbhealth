package kb.health.controller;

import kb.health.controller.response.NutritionStandardResponse;
import kb.health.domain.Member;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.ExerciseRecordResponse;
import kb.health.domain.record.DietRecord;
import kb.health.controller.response.DietRecordResponse;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;
    private final MemberService memberService;

    /**
     * 식단
     */

    // 기록 목록
    @GetMapping("/diet")
    public ResponseEntity<List<DietRecordResponse>> getDietRecordList(@LoginMember CurrentMember currentMember) {
        List<DietRecord> records = recordService.getDietRecords(currentMember.getId());
        List<DietRecordResponse> response = records.stream()
                .map(DietRecordResponse::create)
                .collect(Collectors.toList());

        for(DietRecordResponse dietRecordResponse : response) {
            System.out.println(dietRecordResponse);
        }
        return ResponseEntity.ok(response); // 200 OK와 함께 응답
    }

    // 기록 생성 - 이미지 업로드 포함
    @PostMapping("/diet")
    public ResponseEntity<String> createDietRecord(
            @LoginMember CurrentMember currentMember,
            @RequestPart("record") DietRecordRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String uploadPath = "images";
                String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, imageName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                imageUrl = String.format("/images/%s", imageName);
                request.setDrImgUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("이미지 업로드 실패");
            }
        }

        recordService.saveDietRecord(request, currentMember.getId());
        return ResponseEntity.ok("식단 추가 성공");
    }

    // 특정 기록 조회
    @GetMapping("/diet/{drId}")
    public ResponseEntity<DietRecordResponse> getDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        DietRecord dietRecord = recordService.getDietRecord(drId);
        return ResponseEntity.ok(DietRecordResponse.create(dietRecord)); // 200 OK와 함께 응답
    }

    // 기록 수정 - 이미지 업로드 포함
    @PutMapping("/diet/{drId}")
    public ResponseEntity<String> updateDietRecord(
            @LoginMember CurrentMember currentMember,
            @PathVariable Long drId,
            @RequestPart("record") DietRecordRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String uploadPath = "images";
                String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, imageName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                imageUrl = String.format("/images/%s", imageName);
                request.setDrImgUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("이미지 업로드 실패");
            }
        }

        recordService.updateDietRecord(currentMember.getId(), drId, request);
        return ResponseEntity.ok("식단 수정 성공");
    }

    // 기록 삭제
    @DeleteMapping("/diet/{drId}")
    public ResponseEntity<String> deleteDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        recordService.deleteDietRecord(currentMember.getId(), drId);
        return ResponseEntity.ok("식단 삭제 성공"); // 삭제 성공 메시지
    }

    // 기록 검색
    // 식단 기록 동적 검색
    @GetMapping("/diet/search")
    public ResponseEntity<List<DietRecordResponse>> searchDietRecords(@LoginMember CurrentMember currentMember,
                                                                      @RequestParam(required = false) String menuKeyword,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DietRecord> records = recordService.searchDietRecordsDynamic(currentMember.getId(), menuKeyword, startDate, endDate);
        List<DietRecordResponse> response = records.stream()
                .map(DietRecordResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 일일 권장 영양소 받아오기
    @GetMapping("/ns/{member_account}")
    public ResponseEntity<NutritionStandardResponse> getNS(@LoginMember CurrentMember currentMember) {
        Member member = memberService.findById(currentMember.getId());

        NutritionStandardResponse nutritionStandardResponse = NutritionStandardResponse.create(member.getDailyNutritionStandard());

        return ResponseEntity.ok(nutritionStandardResponse);
    }

    /**
     * 운동
     */

    // 기록 목록
    @GetMapping("/exercise")
    public ResponseEntity<List<ExerciseRecordResponse>> getExerciseRecordList(@LoginMember CurrentMember currentMember) {
        List<ExerciseRecord> records = recordService.getExerciseRecords(currentMember.getId());
        List<ExerciseRecordResponse> response = records.stream()
                .map(ExerciseRecordResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response); // 200 OK와 함께 응답
    }

    // 기록 생성 - 이미지 업로드 포함
    @PostMapping("/exercise")
    public ResponseEntity<String> createExerciseRecord(
            @LoginMember CurrentMember currentMember,
            @RequestPart("record") ExerciseRecordRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String uploadPath = "images";
                String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, imageName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                imageUrl = String.format("/images/%s", imageName);
                request.setErImgUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("이미지 업로드 실패");
            }
        }

        recordService.saveExerciseRecord(request, currentMember.getId());
        return ResponseEntity.ok("운동 추가 성공");
    }

    // 특정 기록 조회
    @GetMapping("/exercise/{exId}")
    public ResponseEntity<ExerciseRecordResponse> getExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        ExerciseRecord exerciseRecord = recordService.getExerciseRecord(exId);
        return ResponseEntity.ok(ExerciseRecordResponse.create(exerciseRecord)); // 200 OK와 함께 응답
    }

    // 기록 수정 - 이미지 업로드 포함
    @PutMapping("/exercise/{exId}")
    public ResponseEntity<String> updateExerciseRecord(
            @LoginMember CurrentMember currentMember,
            @PathVariable Long exId,
            @RequestPart("record") ExerciseRecordRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String uploadPath = "images";
                String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, imageName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                imageUrl = String.format("/images/%s", imageName);
                request.setErImgUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("이미지 업로드 실패");
            }
        }
        System.out.println("이미지 주소");
        System.out.println(imageUrl);

        recordService.updateExerciseRecord(currentMember.getId(), exId, request);
        return ResponseEntity.ok("운동 수정 성공");
    }

    // 기록 삭제
    @DeleteMapping("/exercise/{exId}")
    public ResponseEntity<String> deleteExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        recordService.deleteExerciseRecord(currentMember.getId(), exId);
        return ResponseEntity.ok("운동 삭제 성공"); // 삭제 성공 메시지
    }

    // 운동 기록 동적 검색
    @GetMapping("/exercise/search")
    public ResponseEntity<List<ExerciseRecordResponse>> searchExerciseRecords(@LoginMember CurrentMember currentMember,
                                                                              @RequestParam(required = false) String exerciseName,
                                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ExerciseRecord> records = recordService.searchExerciseRecordsDynamic(currentMember.getId(), exerciseName, startDate, endDate);
        List<ExerciseRecordResponse> response = records.stream()
                .map(ExerciseRecordResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 운동 완료 표기
    @PutMapping("/exercise/{exId}/complete")
    public ResponseEntity<String> markExerciseAsCompleted(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        try {
            recordService.markExerciseAsCompleted(currentMember.getId(), exId);
            return ResponseEntity.ok("운동 완료 처리 성공");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 예외 메시지를 클라이언트에 전달
        }
    }

    // 운동 완료 해제
    @PutMapping("/exercise/{exId}/uncomplete")
    public ResponseEntity<String> unmarkExerciseAsCompleted(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        try {
            recordService.unmarkExerciseAsCompleted(currentMember.getId(), exId);
            return ResponseEntity.ok("운동 완료 해제 성공");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 예외 메시지를 클라이언트에 전달
        }
    }


}