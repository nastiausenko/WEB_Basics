package org.example.lab5.controller.resolvers;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab5.entity.post.Post;
import org.example.lab5.entity.user.User;
import org.example.lab5.service.PostService;
import org.example.lab5.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostQueryResolver {
    private final PostService postService;
    private final UserService userService;

    @QueryMapping
    public List<Post> posts() {
        return postService.getPublicPosts();
    }

    @QueryMapping
    public List<Post> myPosts() {
        return postService.getAllUserPosts();
    }

    @QueryMapping
    public Post postById(@Argument String id) {
        ObjectId postId = new ObjectId(id);
        return postService.getPostById(postId);
    }

    @MutationMapping
    public Post addPost(@Argument String title, @Argument String content, @Argument Boolean isPublic) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();
        return postService.create(post);
    }

    @MutationMapping
    public Post updatePost(
            @Argument String id,
            @Argument String title,
            @Argument String content,
            @Argument Boolean isPublic
    ) {
        ObjectId postId = new ObjectId(id);
        Post updated = Post.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();
        return postService.update(postId, updated);
    }


    @MutationMapping
    public Boolean deletePost(@Argument String id) {
        ObjectId postId = new ObjectId(id);
        postService.delete(postId);
        return true;
    }

    @SchemaMapping(typeName = "Post", field = "user")
    public User getUser(Post post) {
        return userService.getById(post.getUserId());
    }
}
