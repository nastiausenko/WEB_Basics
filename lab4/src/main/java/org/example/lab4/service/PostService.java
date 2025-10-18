package org.example.lab4.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab4.entity.post.Post;
import org.example.lab4.entity.post.PostWithUser;
import org.example.lab4.entity.user.User;
import org.example.lab4.repository.PostRepository;
import org.example.lab4.repository.UserRepository;
import org.example.lab4.security.AccessValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AccessValidator accessValidator;

    public List<Post> getAllUserPosts() {
        User user = accessValidator.getCurrentUser();
        return postRepository.findAllByUserId(user.getId());
    }

    public List<PostWithUser> getPublicPosts() {
        return postRepository.findPublicPostsWithUsername();
    }

    public Post getPostById(ObjectId id) {
        return postRepository.findById(id).orElseThrow(RuntimeException::new);//TODO post exception
    }


    public Post create(Post request) {
        User user = accessValidator.getCurrentUser();
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(request.getIsPublic())
                .createdAt(LocalDateTime.now())
                .userId(user.getId())
                .build();
        postRepository.save(post);

        user.getPosts().add(post);
        userRepository.save(user);

        return post;
    }

    public Post changeVisibility(ObjectId postId) {
        Post post = getPostById(postId);
        accessValidator.validateOwner(post.getUserId());

        boolean isPublic = post.getIsPublic();
        post.setIsPublic(!isPublic);
        postRepository.save(post);
        return post;
    }

    public Post update(ObjectId postId, Post request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        accessValidator.validateOwner(post.getUserId());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublic(request.getIsPublic());
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void delete(ObjectId postId) {
        postRepository.findById(postId).ifPresent(post -> {
            User user = accessValidator.getCurrentUser();
            ObjectId ownerId = post.getUserId();
            accessValidator.validateOwner(ownerId);
            postRepository.deleteById(postId);
            user.getPosts().remove(post);
            userRepository.save(user);
        });
    }
}
