package org.example.lab5.repository;

import org.bson.types.ObjectId;
import org.example.lab5.entity.post.Post;
import org.example.lab5.entity.post.PostWithUser;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    List<Post> findAllByUserId(ObjectId userId);

    @Aggregation(pipeline = {
            "{ $match: { isPublic: true } }",
            "{ $lookup: { from: 'users', localField: 'userId', foreignField: '_id', as: 'user' } }",
            "{ $unwind: '$user' }",
            "{ $project: { title: 1, content: 1, createdAt: 1, username: '$user.username' } }"
    })
    List<PostWithUser> findPublicPostsWithUsername();
}
