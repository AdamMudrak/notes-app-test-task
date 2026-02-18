package org.example.notes_app.dto;

import org.example.notes_app.entity.Tag;

import java.time.LocalDateTime;
import java.util.Set;

public record ResponseNoteDto(
        String id,
        String title,
        String text,
        LocalDateTime createdDateTime,
        Set<Tag> tags
) {
}
