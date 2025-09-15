package com.codecanvas.codeCanvasApp.Repository;

import com.codecanvas.codeCanvasApp.entity.CodeEntry;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CodeEntryRepository extends MongoRepository<CodeEntry, ObjectId> {
    void deleteByauthorId(String authorId);

    List<CodeEntry> findByauthorId(String authorId);

    List<CodeEntry> findByIsPublicTrueAndTagsIn(List<String> tags);

    Page<CodeEntry> findByIsPublicTrue(Pageable pageable);
}
