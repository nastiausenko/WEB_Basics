package org.example.lab5.security.noauth;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.example.lab5.entity.user.User;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mock.user")
public class MockUserProperties {
    private String email = "mockuser@example.com";
    private String username = "Mock User";
    private String password = "Admin2@@3";
    private String id = "68f4ddf2013e75345b1acf76";

    public User toUserEntity() {
        return User.builder()
                .id(new ObjectId(id))
                .email(email)
                .username(username)
                .posts(new ArrayList<>())
                .build();
    }
}