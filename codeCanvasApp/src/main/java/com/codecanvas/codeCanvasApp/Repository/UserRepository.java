package com.codecanvas.codeCanvasApp.Repository;

import com.codecanvas.codeCanvasApp.entity.CodeEntry;
import com.codecanvas.codeCanvasApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String username);

    boolean existsByUserName(String username);
    void deleteByUserName(String username);

    long countByIsActiveTrue();
}
