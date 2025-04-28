package kb.health.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kb.health.Service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.controller.request.LoginRequest;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest , HttpServletResponse response) {
         if(memberService.login(loginRequest.getAccount(), loginRequest.getPassword())){
             Member member = memberService.findMemberByAccount(loginRequest.getAccount());
             String jwtToken = jwtUtil.generateJwtToken(member.getAccount() , member.getId());
             Cookie jwtCookie = new Cookie("jwt", jwtToken);
             jwtCookie.setHttpOnly(true); // JavaScript 접근 방지
             jwtCookie.setSecure(false);   // HTTPS에서만 전송 (개발 시 false 가능)
             jwtCookie.setPath("/");      // 전체 경로에 적용
             jwtCookie.setMaxAge(24 * 60 * 60); // 1일 유효
             response.addCookie(jwtCookie); // 쿠키 추가

             return ResponseEntity.ok("로그인 성공");
         } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
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

    @GetMapping("/regist")
    public String regist() {
        return "regist";
    }

    @PostMapping("/regist")
        public String regist(@ModelAttribute Member member) {
            memberService.save(member);
            return "redirect:/login";
    }

    @ExceptionHandler(MatchException.class)
    public String handleMatchException(MatchException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "redirect:/login";
    }









}
