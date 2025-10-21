package org.example.lab5.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab5.entity.post.Post;
import org.example.lab5.entity.user.User;
import org.example.lab5.repository.UserRepository;
import org.example.lab5.service.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(null);
                    if (user.getPosts() != null) {
                        user.setPosts(user.getPosts().stream()
                                .filter(Post::getIsPublic)
                                .toList());
                    }
                    return user;
                })
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getById(ObjectId id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(null);
                    if (user.getPosts() != null) {
                        user.setPosts(user.getPosts().stream()
                                .filter(Post::getIsPublic)
                                .toList());
                    }
                    return user;
                })
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public List<User> getAllUsersWithPublicPosts() {
        return userRepository.findAll().stream()
                .peek(user -> {
                    user.setPassword(null);
                    if (user.getPosts() != null) {
                        user.setPosts(
                                user.getPosts().stream()
                                        .filter(Post::getIsPublic)
                                        .toList()
                        );
                    }
                })
                .toList();
    }
}
