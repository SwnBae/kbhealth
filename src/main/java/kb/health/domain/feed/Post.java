package kb.health.domain.feed;

import jakarta.persistence.*;
import kb.health.domain.BaseEntity;
import kb.health.domain.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(nullable = false , length = 10000)
    private String content;


    private String imageUrl;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();


    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void addLike(PostLike like) {
        likes.add(like);
        like.setPost(this);
    }

    public int getLikesCount() {
        return likes.size();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void removeLike(PostLike like) {
        likes.remove(like);
        like.setPost(null);
    }
}
