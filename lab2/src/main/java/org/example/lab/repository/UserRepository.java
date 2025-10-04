package org.example.lab.repository;

import org.bson.types.ObjectId;
import org.example.lab.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
}
