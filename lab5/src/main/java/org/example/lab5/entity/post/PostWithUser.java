package org.example.lab5.entity.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostWithUser {
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}