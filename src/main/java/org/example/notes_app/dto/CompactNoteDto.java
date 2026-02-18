package org.example.notes_app.dto;

import java.time.LocalDate;

public record CompactNoteDto(
        String title,
        LocalDate createdDate) {}
