package kb.health.controller.response;

import kb.health.domain.feed.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor

public class CommentResponse {

    private Long commentId;
    private String writer;
    private String writerProfileImage;
    private String comment;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getWriter().getUserName(),
                comment.getWriter().getProfileImageUrl(),
                comment.getText(),
                comment.getCreatedDate()
        );
    }



}


