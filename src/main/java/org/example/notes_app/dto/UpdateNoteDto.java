package org.example.notes_app.dto;

import org.example.notes_app.entity.Tag;

import java.util.Set;

public record UpdateNoteDto(
        String title,
        String text,
        Set<Tag> tags
){}
