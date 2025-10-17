package org.example.lab4.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab4.entity.post.Post;
import org.example.lab4.entity.post.PostWithUser;
import org.example.lab4.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/user-posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllUserPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostId(@PathVariable ObjectId postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<PostWithUser>> getPublicPosts() {
        return ResponseEntity.ok(postService.getPublicPosts());
    }

    @PostMapping("/add-post")
    public ResponseEntity<Post> save(@RequestBody Post post) {
        return ResponseEntity.ok(postService.create(post));
    }

    @PutMapping("/{postId}/edit")
    public ResponseEntity<Post> update(@PathVariable ObjectId postId, @RequestBody Post post) {
        return ResponseEntity.ok(postService.update(postId, post));
    }

    @PutMapping("/{postId}/visibility")
    public ResponseEntity<Post> visibility(@PathVariable ObjectId postId) {
        return ResponseEntity.ok(postService.changeVisibility(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable ObjectId postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
