package kb.health.controller;


import kb.health.Service.MemberService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.MemberEditRequest;
import kb.health.controller.response.MemberResponse;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final MemberService memberService;

    //멤버 조회
    //멤버DTO로 반환하도록 수정!
    @GetMapping("/profile/{member_account}")
    public ResponseEntity<MemberResponse> getProfile(@PathVariable("member_account") String member_account) {
        MemberResponse memberResponse = memberService.findMemberByAccount(member_account);
        return ResponseEntity.ok(memberResponse);
    }

    // 이미지 주소 요청
//    @GetMapping("/profile/{member_account}/image")
//    public ResponseEntity<Member> getProfileImage(@PathVariable("member_account") String member_account) {
//
//    }

    // 프로필 수정
    @PostMapping("/profile/edit")
    public ResponseEntity<?> updateProfile(@LoginMember CurrentMember currentMember, @RequestBody MemberEditRequest memberEditRequest) {
        memberService.updateMember(currentMember.getId(), memberEditRequest);
        return ResponseEntity.ok("회원정보 수정 성공");
    }






}
