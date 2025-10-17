package org.example.lab4.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab4.entity.Post;
import org.example.lab4.entity.PostWithUser;
import org.example.lab4.entity.User;
import org.example.lab4.repository.PostRepository;
import org.example.lab4.repository.UserRepository;
import org.example.lab4.security.AccessValidator;
import org.example.lab4.service.exceptions.UserNotFoundException;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        User user = getCurrentUser();
        return postRepository.findAllByUserId(user.getId());
    }

    public List<PostWithUser> getPublicPosts() {
        return postRepository.findPublicPostsWithUsername();
    }

    public Post getPostById(ObjectId id) {
        return postRepository.findById(id).orElseThrow(RuntimeException::new);//TODO post exception
    }


    public Post create(Post request) {
        User user = getCurrentUser();
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
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
        User user = getCurrentUser();
        accessValidator.validateOwner(user.getId(), post.getUserId());

        boolean isPublic = post.getIsPublic();
        post.setIsPublic(!isPublic);
        postRepository.save(post);
        return post;
    }

    public Post update(ObjectId postId, Post request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = getCurrentUser();
        accessValidator.validateOwner(post.getUserId(), user.getId());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPublic(request.getIsPublic());
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void delete(ObjectId postId) {
        postRepository.findById(postId).ifPresent(post -> {
            User user = getCurrentUser();
            ObjectId ownerId = post.getUserId();
            accessValidator.validateOwner(ownerId, user.getId());
            postRepository.deleteById(postId);
            user.getPosts().remove(post);
            userRepository.save(user);
        });
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
