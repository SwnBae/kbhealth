package kb.health.domain.feed;

import jakarta.persistence.*;
import kb.health.domain.Member;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static PostLike create(Member member, Post post) {
        PostLike like = new PostLike();
        like.setMember(member);
        like.setPost(post);
        member.addPostLike(like);
        post.addLike(like);
        return like;
    }


}
