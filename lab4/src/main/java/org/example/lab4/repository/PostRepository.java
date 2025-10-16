package org.example.lab4.repository;

import org.bson.types.ObjectId;
import org.example.lab4.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    Post findByUserId(String userId);
}
