package org.example.lab4.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class Post {
    @Id
    private ObjectId id;

    private ObjectId userId;
    private String title;
    private String content;
    private Boolean isPublic;
    private LocalDateTime createdAt = LocalDateTime.now();
}
