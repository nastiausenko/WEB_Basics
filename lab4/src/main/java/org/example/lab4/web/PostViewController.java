package org.example.lab4.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v2/posts")
public class PostViewController {
    @GetMapping("/public")
    public String homePage() {
        return "index";
    }

    @GetMapping("/user-posts")
    public String postsPage() {
        return "posts";
    }

    @GetMapping("/add-post")
    public String addPostPage() {
        return "add_post";
    }

    @GetMapping("/edit-post")
    public String editPostPage() {
        return "edit_post";
    }
}
