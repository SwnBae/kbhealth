package kb.health.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.exception.LoginException;
import kb.health.service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.controller.request.LoginRequest;
import kb.health.controller.request.MemberRegistRequest;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        if (memberService.login(loginRequest.getAccount(), loginRequest.getPassword())) {
            Member member = memberService.getMemberByAccount(loginRequest.getAccount());
            String jwtToken = jwtUtil.generateJwtToken(member.getAccount(), member.getId());
            Cookie jwtCookie = new Cookie("jwt", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);
            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "redirect", "/"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인 실패"));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/regist")
    public ResponseEntity<?> regist(@RequestBody @Valid MemberRegistRequest memberRegistRequest) {
        memberService.save(memberRegistRequest);
        return ResponseEntity.ok(Map.of(
                "message", "회원가입 성공",
                "redirect", "/login"
        ));
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<?> checkLogin(@LoginMember CurrentMember currentMember) {
        if (currentMember == null) {
            throw LoginException.loginProcessNeeded();
        }
        return ResponseEntity.ok(Map.of(
                "id", currentMember.getId(),
                "account", currentMember.getAccount()
        ));
    }

    // 아이디 중복 검사
    @GetMapping("/check-account")
    @ResponseBody
    public ResponseEntity<?> checkAccountDuplicate(@RequestParam String account) {
        try {
            if (account == null || account.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.ACCOUNT_EMPTY.getCode()
                ));
            }

            if (account.length() < 4 || account.length() > 20) {
                return ResponseEntity.ok().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.ACCOUNT_LENGTH_INVALID.getCode()
                ));
            }

            // (필요시) 영문+숫자만 허용하려면 아래처럼 추가 검증 후 FORMAT_INVALID 사용
            // if (!account.matches("^[A-Za-z0-9]+$")) {
            //     return ResponseEntity.ok().body(Map.of(
            //         "available", false,
            //         "code", RegistValidationCode.ACCOUNT_FORMAT_INVALID.getCode()
            //     ));
            // }

            boolean isDuplicate = memberService.isAccountDuplicate(account);
            if (isDuplicate) {
                return ResponseEntity.ok().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.ACCOUNT_DUPLICATE.getCode()
                ));
            } else {
                return ResponseEntity.ok().body(Map.of(
                        "available", true,
                        "code", RegistValidationCode.ACCOUNT_AVAILABLE.getCode()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "available", false,
                    "code", RegistValidationCode.SERVER_ERROR.getCode()
            ));
        }
    }

    // 닉네임 중복 검사
    @GetMapping("/check-username")
    @ResponseBody
    public ResponseEntity<?> checkUsernameDuplicate(@RequestParam String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.USERNAME_EMPTY.getCode()
                ));
            }

            if (username.length() < 2 || username.length() > 10) {
                return ResponseEntity.ok().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.USERNAME_LENGTH_INVALID.getCode()
                ));
            }

            // (필요시) 특수문자 불가 검증 예시
            // if (!username.matches("^[가-힣A-Za-z0-9]+$")) {
            //     return ResponseEntity.ok().body(Map.of(
            //         "available", false,
            //         "code", RegistValidationCode.USERNAME_FORMAT_INVALID.getCode()
            //     ));
            // }

            boolean isDuplicate = memberService.isUsernameDuplicate(username);
            if (isDuplicate) {
                return ResponseEntity.ok().body(Map.of(
                        "available", false,
                        "code", RegistValidationCode.USERNAME_DUPLICATE.getCode()
                ));
            } else {
                return ResponseEntity.ok().body(Map.of(
                        "available", true,
                        "code", RegistValidationCode.USERNAME_AVAILABLE.getCode()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "available", false,
                    "code", RegistValidationCode.SERVER_ERROR.getCode()
            ));
        }
    }
}
