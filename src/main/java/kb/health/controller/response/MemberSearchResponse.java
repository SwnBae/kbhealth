package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSearchResponse {

    private Long memberId;
    private String account;
    private String userName;
    private String profileImageUrl;

    public static MemberSearchResponse create(Member member) {
        return new MemberSearchResponse(member.getId(), member.getAccount(), member.getUserName(), member.getProfileImageUrl());
    }
}
