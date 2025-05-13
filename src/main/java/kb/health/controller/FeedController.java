package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.CommentCreateRequest;
import kb.health.controller.request.PostCreateRequest;
import kb.health.controller.request.PostEditRequest;
import kb.health.controller.response.CommentResponse;
import kb.health.controller.response.PostResponse;
import kb.health.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController {
    private final FeedService feedService;
    private final JwtUtil jwtUtil;

    //피드 조회
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getFeed(@LoginMember CurrentMember currentMember, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(feedService.getPosts(currentMember.getId() , page, size));
    }

    //개인 피드 조회
    @GetMapping("/my")
    public ResponseEntity<Page<PostResponse>> getMyFeed(@LoginMember CurrentMember currentMember, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(feedService.getPostsBySelf(currentMember.getId() , page, size));
    }

    //포스트 작성
    @PostMapping
    public ResponseEntity<?> createPost(@LoginMember CurrentMember currentMember, @RequestBody PostCreateRequest postCreateRequest) {
        feedService.savePost(currentMember.getId() , postCreateRequest);
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
    public ResponseEntity<?> createComment(@LoginMember CurrentMember currentMember,@PathVariable Long post_id , @RequestBody CommentCreateRequest commentCreateRequest) {
        feedService.saveComment(currentMember.getId(), post_id , commentCreateRequest);
        return ResponseEntity.ok("댓글 작성 완료");
    }

    //댓글 삭제
    @DeleteMapping("/{post_id}/comment/{comment_id}")
    public ResponseEntity<?> deleteComment(@LoginMember CurrentMember currentMember, @PathVariable Long post_id, @PathVariable Long comment_id) {
        feedService.deleteComment(currentMember.getId(), comment_id);
        return ResponseEntity.ok("댓글 삭제 완료");
    }

    //좋아요 토글
    @GetMapping("/{post_id}/like")
    public ResponseEntity<Boolean> likePost(@LoginMember CurrentMember currentMember, @PathVariable Long post_id) {
        return ResponseEntity.ok(feedService.postLikeToggle(currentMember.getId(), post_id));
    }

}
