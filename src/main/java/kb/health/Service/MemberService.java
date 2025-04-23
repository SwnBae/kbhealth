package kb.health.Service;

import kb.health.Repository.FollowRepository;
import kb.health.Repository.MemberRepository;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long myId, Long targetId) {
        //영속화
        Member me = memberRepository.findById(myId);
        Member target = memberRepository.findById(targetId);

        // 자기 자신을 팔로우하지 않도록 검증
        if (me.equals(target)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        // 팔로우 객체 생성
        Follow follow = Follow.createFollow(me, target);

        // 팔로우 엔티티 저장
        followRepository.save(follow);
    }
}
