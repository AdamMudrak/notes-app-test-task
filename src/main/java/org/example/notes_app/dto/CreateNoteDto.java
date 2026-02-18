package org.example.notes_app.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.notes_app.entity.Tag;

import java.util.Set;

public record CreateNoteDto(
        @NotBlank
        String title,
        @NotBlank
        String text,
        Set<Tag> tags
){}
