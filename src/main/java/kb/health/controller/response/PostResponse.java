package kb.health.controller.response;

import kb.health.domain.feed.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private Long writerId;
    private String writerName;
    private String writerProfileImage;
    private String writerAccount;
    private double healthScore;
    private int likeCount;
    private int commentCount;
    private boolean liked;
    private LocalDateTime createdAt;


    public static PostResponse from(Post post, boolean liked, int likeCount, int commentCount) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getWriter().getId(),
                post.getWriter().getUserName(),
                post.getWriter().getProfileImageUrl(),
                post.getWriter().getAccount(),
                post.getWriter().getBaseScore(),
                likeCount,
                commentCount,
                liked,
                post.getCreatedDate()
        );
    }


}
