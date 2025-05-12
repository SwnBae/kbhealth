package kb.health.service;

import kb.health.controller.request.CommentCreateRequest;
import kb.health.controller.request.PostCreateRequest;
import kb.health.controller.request.PostEditRequest;
import kb.health.controller.response.PostResponse;
import kb.health.domain.Member;
import kb.health.domain.feed.Comment;
import kb.health.domain.feed.Post;
import kb.health.repository.FollowRepository;
import kb.health.repository.MemberRepository;
import kb.health.repository.feed.CommentRepository;
import kb.health.repository.feed.PostLikeRepository;
import kb.health.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Writer;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    //피드 갱신 메서드
    public Page<PostResponse> getPosts(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 1. 내가 팔로우한 유저들의 ID
        List<Long> followingIds = followRepository.findFollowings(memberId);

        // 2. 게시글 페이징 조회
        Page<Post> postPage = postRepository.findByWriterIdInOrderByCreatedDateDesc(followingIds, pageable);

        // 3. 게시글 ID 리스트 추출
        List<Long> postIds = postPage.stream()
                .map(Post::getId)
                .toList();

        // 4. 내가 좋아요 누른 게시글 ID들 조회
        Set<Long> likedPostIds = new HashSet<>(postLikeRepository.findPostIdsLikedByMember(memberId));

        // 5. 게시글별 좋아요 수 조회
        Map<Long, Integer> likeCountMap = new HashMap<>();
        postLikeRepository.countLikesByPostIds(postIds)
                .forEach(row -> likeCountMap.put((Long) row[0], ((Long) row[1]).intValue()));

        // 6. 게시글별 댓글 수 조회
        Map<Long, Integer> commentCountMap = new HashMap<>();
        commentRepository.countCommentsByPostIds(postIds)
                .forEach(row -> commentCountMap.put((Long) row[0], ((Long) row[1]).intValue()));

        // 7. DTO 변환
        return postPage.map(post -> {
            Long postId = post.getId();
            boolean liked = likedPostIds.contains(postId);
            int likeCount = likeCountMap.getOrDefault(postId, 0);
            int commentCount = commentCountMap.getOrDefault(postId, 0);
            return PostResponse.from(post, liked, likeCount, commentCount);
        });
    }

    //게시글 작성 메서드
    @Transactional
    public Long savePost(Long memberId, PostCreateRequest postCreateRequest ) {
        Member writer = memberRepository.findMemberById(memberId);
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .writer(writer).content(postCreateRequest.getContent())
                .imageUrl(postCreateRequest.getImageUrl())
                .build();
        postRepository.save(post);
        return post.getId();
    }

    //게시글 수정 메서드
    @Transactional
    public Long editPost(Long memberId, Long postId , PostEditRequest postEditRequest ) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!post.getWriter().getId().equals(memberId)) {
            throw new RuntimeException("<UNK> <UNK> <UNK> <UNK>.");
        }
        post.setContent(postEditRequest.getContent());
        post.setImageUrl(postEditRequest.getImageUrl());
        return post.getId();
    }

    //게시글 삭제 메서드
    @Transactional
    public Long deletePost(Long memberId ,Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("<UNK> <UNK> <UNK> <UNK>."));
        if (!post.getWriter().getId().equals(memberId)) {
            throw new RuntimeException("<UNK> <UNK> <UNK> <UNK>.");
        }
        postRepository.delete(post);
        return post.getId();
    }

    //댓글 작성 메서드
    @Transactional
    public Long saveComment(Long memberId, Long postId, CommentCreateRequest createCommentRequest ) {
        Member writer = memberRepository.findMemberById(memberId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("<UNK> <UNK> <UNK> <UNK>."));
        Comment comment = Comment.builder().writer(writer).post(post).text(createCommentRequest.getComment()).build();
        commentRepository.save(comment);
        return comment.getId();
    }

    //댓글 삭제 메서드
    @Transactional
    public Long deleteComment(Long memberId ,Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("<UNK> <UNK> <UNK> <UNK>."));
        if(!comment.getWriter().getId().equals(memberId)) {
            throw new RuntimeException("<UNK> <UNK> <UNK> <UNK>.");
        }
        commentRepository.delete(comment);
        return comment.getId();
    }

    //좋아요 추가 메서드
}
