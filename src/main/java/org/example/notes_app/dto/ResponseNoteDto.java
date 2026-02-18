package org.example.notes_app.dto;

import org.example.notes_app.entity.Tag;

import java.time.LocalDate;
import java.util.Set;

public record ResponseNoteDto(
        String id,
        String title,
        String text,
        LocalDate createdDate,
        Set<Tag> tags
) {
}
