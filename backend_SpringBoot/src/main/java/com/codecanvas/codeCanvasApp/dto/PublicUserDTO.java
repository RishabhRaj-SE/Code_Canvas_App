package com.codecanvas.codeCanvasApp.dto;

import com.codecanvas.codeCanvasApp.Service.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDTO {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String userName;
    private String about;
    private Boolean isActive;
    private java.util.List<String> roles;
}
