package org.example.notes_app.dto;

import java.time.LocalDateTime;

public record CompactNoteDto(
        String title,
        LocalDateTime createdDateTime) {}
