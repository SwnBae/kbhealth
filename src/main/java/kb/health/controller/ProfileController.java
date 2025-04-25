package kb.health.controller;


import kb.health.Service.MemberService;
import kb.health.authentication.JwtUtil;
import kb.health.domain.Member;
import kb.health.domain.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{member_id}")
    public String profile(@PathVariable("member_id") String memberId, Model model) {

    }

    @GetMapping("/{member_id}/edit")
    public String edit(@PathVariable("member_id") String memberId, Model model) {

    }

    @PostMapping("/{member_id}/edit")
    public String editRequset(@PathVariable("member_id") long memberId, @ModelAttribute MemberForm memberForm, Model model) {
        memberService.updateMember(memberId , memberForm);
        return "redirect:/profile/" + memberId;
    }




}
