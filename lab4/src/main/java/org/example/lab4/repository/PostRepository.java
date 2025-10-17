package org.example.lab4.repository;

import org.bson.types.ObjectId;
import org.example.lab4.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    List<Post> findAllByUserId(ObjectId userId);
    List<Post> findAllByIsPublic(boolean isPublic);
}
