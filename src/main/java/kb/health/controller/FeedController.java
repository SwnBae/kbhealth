package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.CommentCreateRequest;
import kb.health.controller.request.PostCreateRequest;
import kb.health.controller.request.PostEditRequest;
import kb.health.controller.response.CommentResponse;
import kb.health.controller.response.PostResponse;
import kb.health.domain.feed.Post;
import kb.health.domain.notification.NotificationType;
import kb.health.exception.FeedException;
import kb.health.exception.ImageException;
import kb.health.exception.NotificationException;
import kb.health.service.FeedService;
import kb.health.service.MemberService;
import kb.health.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService feedService;
    private final NotificationService notificationService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    //피드 조회
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getFeed(@LoginMember CurrentMember currentMember, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(feedService.getPosts(currentMember.getId() , page, size));
    }

    //개별 피드 조회
    @GetMapping("/{member_account}/feed")
    public ResponseEntity<Page<PostResponse>> getMyFeed(@PathVariable("member_account") Long memberId, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(feedService.getPostsBySelf(memberId , page, size));
    }

    //포스트 작성
    @PostMapping
    public ResponseEntity<?> createPost(@LoginMember CurrentMember currentMember,
                                        @RequestPart("post") PostCreateRequest postCreateRequest,
                                        @RequestPart(value = "image",required = false) MultipartFile image) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try{
                String uploadPath = "images";
                String imageName = UUID.randomUUID()+"_"+image.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, imageName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                imageUrl = String.format("/images/%s", imageName);
            } catch (Exception e){
                throw ImageException.imageUploadFail();
            }
        }
        feedService.savePost(currentMember.getId() , postCreateRequest, imageUrl);
        return ResponseEntity.ok("게시글 작성 완료");
    }

    //포스트 수정
    @PutMapping("/{post_id}")
    public ResponseEntity<?> editPost(@LoginMember CurrentMember currentMember, @PathVariable Long post_id, @RequestBody PostEditRequest postEditRequest) {
        feedService.editPost(currentMember.getId() , post_id , postEditRequest);
        return ResponseEntity.ok("게시글 수정 완료");
    }

    //포스트 삭제
    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@LoginMember CurrentMember currentMember, @PathVariable long post_id) {
        feedService.deletePost(currentMember.getId() , post_id);
        return ResponseEntity.ok("게시글 삭제 완료");
    }

    //댓글 페이징
    @GetMapping("/{post_id}/commentList")
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable Long post_id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(feedService.getComments(post_id, page, size));
    }

    //댓글 작성
    @PostMapping("/{post_id}/comment")
    public ResponseEntity<?> createComment(@LoginMember CurrentMember currentMember, @PathVariable Long post_id , @RequestBody CommentCreateRequest commentCreateRequest) {
        Long commentId = feedService.saveComment(currentMember.getId(), post_id , commentCreateRequest);

        // 알림 생성: 게시글 작성자에게 댓글 알림
        Post post = feedService.getPost(post_id)
                .orElseThrow(FeedException::canNotFindPost);
        Long postWriterId = post.getWriter().getId();

        notificationService.createCommentNotification(
                currentMember.getId(),
                postWriterId,
                commentId,
                post.getTitle(),
                commentCreateRequest.getComment()
        );

        return ResponseEntity.ok("댓글 작성 완료");
    }

    //댓글 삭제
    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ResponseEntity<?> deleteComment(@LoginMember CurrentMember currentMember, @PathVariable Long post_id, @PathVariable Long comment_id) {
        feedService.deleteComment(currentMember.getId(), comment_id);

        Post post = feedService.getPost(post_id)
                .orElseThrow(FeedException::canNotFindPost);

        Long postWriterId = post.getWriter().getId();

        notificationService.deleteNotification(currentMember.getId(), postWriterId, NotificationType.COMMENT, comment_id);

        return ResponseEntity.ok("댓글 삭제 완료");
    }

    //좋아요 토글
    @PutMapping("/{post_id}/like")
    public ResponseEntity<Boolean> likePost(@LoginMember CurrentMember currentMember, @PathVariable Long post_id) {
        Boolean isLiked = feedService.postLikeToggle(currentMember.getId(), post_id);

        Post post = feedService.getPost(post_id)
                .orElseThrow(FeedException::canNotFindPost);
        Long postWriterId = post.getWriter().getId();

        if(isLiked) {
            notificationService.createLikeNotification(
                    currentMember.getId(),
                    postWriterId,
                    post_id,
                    post.getTitle()
            );
        } else {
            notificationService.deleteNotification(currentMember.getId(), postWriterId, NotificationType.LIKE,post_id);
        }

        return ResponseEntity.ok(isLiked);
    }

    //단건 게시글 상세 조회
    @GetMapping("/post/{post_id}")
    public ResponseEntity<PostResponse> getPostDetail(
            @LoginMember CurrentMember currentMember,
            @PathVariable Long post_id) {

        PostResponse postDetail = feedService.getPostDetail(currentMember.getId(), post_id);
        return ResponseEntity.ok(postDetail);
    }
}
