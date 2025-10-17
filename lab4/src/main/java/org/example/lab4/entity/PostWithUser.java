package org.example.lab4.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostWithUser {
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}