package kb.health.controller;
import kb.health.Service.MemberService;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "login";
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
        return "redirect:/login";
    }









}
