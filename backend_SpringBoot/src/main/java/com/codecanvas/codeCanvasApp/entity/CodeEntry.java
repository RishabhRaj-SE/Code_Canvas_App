package com.codecanvas.codeCanvasApp.entity;

import com.codecanvas.codeCanvasApp.Service.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
@Document(collection="Code_Entries")
@Data
@NoArgsConstructor
public class CodeEntry {

    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;

    private String authorId;

    private String authorName;

    @NonNull
    private Boolean isPublic;

    @NonNull
    private String title;

    private String description;

    private String content;

    private List<String> tags=new ArrayList<>();


    private LocalDateTime date;

    private List<Comment> comments=new ArrayList<>();

    private List<ObjectId> likedByUserIds=new ArrayList<>();





}
