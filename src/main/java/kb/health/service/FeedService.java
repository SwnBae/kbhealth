package kb.health.service;

import kb.health.controller.response.PostResponse;
import kb.health.domain.feed.Post;
import kb.health.repository.FollowRepository;
import kb.health.repository.feed.CommentRepository;
import kb.health.repository.feed.PostLikeRepository;
import kb.health.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

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

}
