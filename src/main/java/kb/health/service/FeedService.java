package kb.health.service;

import kb.health.controller.request.CommentCreateRequest;
import kb.health.controller.request.PostCreateRequest;
import kb.health.controller.request.PostEditRequest;
import kb.health.controller.response.CommentResponse;
import kb.health.controller.response.PostResponse;
import kb.health.domain.Member;
import kb.health.domain.feed.Comment;
import kb.health.domain.feed.Post;
import kb.health.domain.feed.PostLike;
import kb.health.exception.FeedException;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public Optional<Post> getPost(Long postId) {
        return postRepository.findById(postId);
    }

    public Optional<Comment> getCommentWithPost(Long commentId) {
        // fetch join을 사용해서 Post도 함께 조회
        return commentRepository.findByIdWithPost(commentId);
    }

    //피드 갱신 메서드
    public Page<PostResponse> getPosts(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        //내가 팔로우한 유저들의 ID
        List<Long> followingIds = followRepository.findFollowingIds(memberId);

        //게시글 페이징 조회
        Page<Post> postPage = postRepository.findByWriterIdInOrderByCreatedDateDesc(followingIds, pageable);

        return convertToPostResponsePage(memberId, postPage);
    }

    public Page<PostResponse> getPostsBySelf(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        //내 게시글 페이징 조회
        Page<Post> postPage = postRepository.findByWriterIdOrderByCreatedDateDesc(memberId, pageable);

        return convertToPostResponsePage(memberId, postPage);
    }

    public Page<PostResponse> convertToPostResponsePage(Long memberId, Page<Post> postPage) {
        //게시글 ID 리스트 추출
        List<Long> postIds = postPage.stream()
                .map(Post::getId)
                .toList();

        Set<Long> likedPostIds = new HashSet<>(postLikeRepository.findPostIdsLikedByMember(memberId));

        //게시글별 좋아요 수 조회
        Map<Long, Integer> likeCountMap = new HashMap<>();
        postLikeRepository.countLikesByPostIds(postIds)
                .forEach(row -> likeCountMap.put((Long) row[0], ((Long) row[1]).intValue()));

        //게시글별 댓글 수 조회
        Map<Long, Integer> commentCountMap = new HashMap<>();
        commentRepository.countCommentsByPostIds(postIds)
                .forEach(row -> commentCountMap.put((Long) row[0], ((Long) row[1]).intValue()));

        //DTO 변환
        return postPage.map(post -> {
            Long postId = post.getId();
            boolean liked = likedPostIds.contains(postId);
            int likeCount = likeCountMap.getOrDefault(postId, 0);
            int commentCount = commentCountMap.getOrDefault(postId, 0);
            return PostResponse.from(post, liked, likeCount, commentCount);
        });
    }

    //댓글 페이지 갱신 메서드
    public Page<CommentResponse> getComments(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreatedDateDesc(postId, pageable);
        return commentPage.map(
                CommentResponse::from
        );
    }

    // 게시글 단건 호출
    public PostResponse getPostDetail(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(FeedException::canNotFindPost);

        boolean liked = false;
        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            liked = postLikeRepository.findByMemberAndPost(member, post).isPresent();
        }

        // 간단하게 호출
        int likeCount = postLikeRepository.countByPost(post);
        int commentCount = commentRepository.countByPost(post);

        return PostResponse.from(post, liked, likeCount, commentCount);
    }

    //게시글 작성 메서드
    @Transactional
    public Long savePost(Long memberId, PostCreateRequest postCreateRequest , String imageUrl) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .writer(writer).content(postCreateRequest.getContent())
                .imageUrl(imageUrl)
                .build();
        postRepository.save(post);

        return post.getId();
    }

    //게시글 수정 메서드
    @Transactional
    public void editPost(Long memberId, Long postId , PostEditRequest postEditRequest ) {
        Post post = postRepository.findById(postId).orElseThrow(FeedException::canNotFindPost);
        if (!post.getWriter().getId().equals(memberId)) {
            throw FeedException.unauthorizeAccess();
        }
        post.setTitle(postEditRequest.getTitle());
        post.setContent(postEditRequest.getContent());
    }

    //게시글 삭제 메서드
    @Transactional
    public void deletePost(Long memberId ,Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(FeedException::canNotFindPost);
        if (!post.getWriter().getId().equals(memberId)) {
            throw FeedException.unauthorizeAccess();
        }
        postRepository.delete(post);
    }


    //댓글 작성 메서드
    @Transactional
    public Long saveComment(Long memberId, Long postId, CommentCreateRequest createCommentRequest ) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Post post = postRepository.findById(postId).orElseThrow(FeedException::canNotFindPost);
        Comment comment = Comment.builder().writer(writer).post(post).text(createCommentRequest.getComment()).build();
        commentRepository.save(comment);

        return comment.getId();
    }

    //댓글 삭제 메서드
    @Transactional
    public void deleteComment(Long memberId ,Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(FeedException::canNotFindComment);
        if(!comment.getWriter().getId().equals(memberId)) {
            throw FeedException.unauthorizeAccess();
        }
        commentRepository.delete(comment);
    }

    //좋아요 추가 메서드
    @Transactional
    public Boolean postLikeToggle(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(FeedException::canNotFindPost);
        Optional<PostLike> postLike = postLikeRepository.findByMemberAndPost(member, post);

        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            return false;
        } else {
            PostLike postLikeAdd = PostLike.create(member, post);
            postLikeRepository.save(postLikeAdd);
            return true;
        }
    }
}
