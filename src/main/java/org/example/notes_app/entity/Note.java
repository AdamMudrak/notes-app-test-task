package org.example.notes_app.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "notes")
@Getter
@Setter
public class Note {
    @Id
    private String id;
    private String title;
    private String text;
    private LocalDateTime createdDateTime;
    private String username;
    private Set<Tag> tags;
}
