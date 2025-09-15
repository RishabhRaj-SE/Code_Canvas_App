package com.codecanvas.codeCanvasApp.entity;

import com.codecanvas.codeCanvasApp.Service.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="Users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;

    @Indexed(unique = true)

    @NonNull//saare unique username honge, yesb lombook ka function hai
    private String userName;
    private String email;
    private String about;
    private Boolean isActive;
    @NonNull
    private String password;
    @DBRef
    private List<CodeEntry> codeEntries=new ArrayList<>();
    @DBRef
    private List<CodeEntry> savedEntries=new ArrayList<>();

    private List<String> roles;

}
