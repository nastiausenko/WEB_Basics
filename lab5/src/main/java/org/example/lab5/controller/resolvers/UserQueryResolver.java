package org.example.lab5.controller.resolvers;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab5.entity.user.User;
import org.example.lab5.security.AccessValidator;
import org.example.lab5.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserQueryResolver {
    private final UserService userService;
    private final AccessValidator accessValidator;

    @QueryMapping
    public List<User> allUsers() {
        return userService.getAllUsersWithPublicPosts();
    }

    @QueryMapping
    public User userById(@Argument String id) {
        ObjectId userId = new ObjectId(id);
        return userService.getById(userId);
    }

    @QueryMapping
    public User userByUsername(@Argument String username) {
        return userService.getByUsername(username);
    }

    @QueryMapping
    public User me() {
        return accessValidator.getCurrentUser();
    }
}
