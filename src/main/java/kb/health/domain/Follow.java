package kb.health.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private Member from; //자신
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private Member to; //팔로우 한 사람

    /* 빌더 */
    public static Follow createFollow(Member from, Member to) {
        Follow follow = new Follow();
        follow.setFrom(from);
        follow.setTo(to);

        return follow;
    }

    /* 연관관계 편의 메서드 */
    private void setFrom(Member from) {
        if (this.from != null) {
            this.from.getFollowings().remove(this);  // 기존 follower에서 제거
        }
        this.from = from;
        from.getFollowings().add(this);  // 새로운 follower에 추가
    }

    private void setTo(Member to) {
        if (this.to != null) {
            this.to.getFollowers().remove(this);  // 기존 following에서 제거
        }
        this.to = to;
        to.getFollowers().add(this);  // 새로운 following에 추가
    }

    public void disconnect() {
        this.from.getFollowings().remove(this);
        this.to.getFollowers().remove(this);
    }
}
