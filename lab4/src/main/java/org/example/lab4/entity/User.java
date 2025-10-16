package org.example.lab4.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class User {
    @Id
    private ObjectId id;

    private String email;
    private String username;
    private String password;

    @DocumentReference
    private List<Post> posts;
}
