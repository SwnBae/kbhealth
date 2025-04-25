package kb.health.controller;
import kb.health.Service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.controller.request.LoginRequest;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();

    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        return "";
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
        switch ()
        return "redirect:/login";
    }









}
