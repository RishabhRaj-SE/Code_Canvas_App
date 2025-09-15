package com.codecanvas.codeCanvasApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Comment {
    private ObjectId commenterId;
    private String commenterUsername;
    private String text;
    private LocalDateTime commentedAt = LocalDateTime.now();


}
