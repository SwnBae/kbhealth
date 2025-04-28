package kb.health.controller;

import jakarta.validation.Valid;
import kb.health.Service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.domain.Member;
import kb.health.domain.request.MemberRegistRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 토큰 테스트용
 */

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Member member = memberService.findMemberByAccount(loginRequest.account());

        if (member == null || !member.getPassword().equals(loginRequest.password())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다."); // 필요하면 커스텀 예외로 변경 가능
        }

        String token = jwtUtil.generateJwtToken(member.getAccount(), member.getId());

        return new TokenResponse(token);
    }

    // 회원가입 API
    @PostMapping("/api/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid MemberRegistRequest memberRegistRequest) {
        // 회원가입 처리
        memberService.save(memberRegistRequest);

        // 회원가입 성공 메시지 반환
        return ResponseEntity.ok("회원가입 성공");
    }

    public record LoginRequest(String account, String password) {}
    public record TokenResponse(String accessToken) {}
}
