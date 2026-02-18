package org.example.notes_app.controller;

import org.example.notes_app.dto.*;
import org.example.notes_app.service.NoteService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Operations for managing notes")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @Operation(summary = "Create a note", description = "User should to be able to create notes that have "
            + "Title, Created Date, Text and Tags that can be empty. "
            + "The only allowed tags are “BUSINESS”, “PERSONAL” and “IMPORTANT”")
    public ResponseNoteDto createNote(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Note creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateNoteDto.class))
            ) CreateNoteDto createNoteDto
    ) {
        return noteService.createNote(user.getUsername(), createNoteDto);
    }

    @PutMapping("/{noteId}")
    @Operation(summary = "Update a note", description = "User should be always able to update notes.")
    public ResponseNoteDto updateNote(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Note id", required = true) @PathVariable String noteId,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Note update payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateNoteDto.class))
            ) UpdateNoteDto updateNoteDto
    ) {
        return noteService.updateNote(user.getUsername(), noteId, updateNoteDto);
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note", description = "User should be always able to delete notes.")
    public void deleteNote(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Note id", required = true) @PathVariable String noteId
    ) {
        noteService.deleteNote(user.getUsername(), noteId);
    }

    @GetMapping
    @Operation(summary = "Get notes (paged)", description = "While listing the notes the user should be able to filter "
            + "by “Tags”. Notes should always be sorted in the way the user would see the newest first."
            + "Listing notes should support pagination as the user might have a lot of notes.")
    public List<ResponseNoteDto> getAllNotesByUsername(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Filter parameters (bound from query params)") FilterDto filterDto,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable
    ) {
        return noteService.getAllNotesByUsername(user.getUsername(), filterDto, pageable);
    }

    @GetMapping("/{noteId}/stats")
    @Operation(summary = "Get note stats", description = "The app should allow to obtain stats per each note that would "
            + "calculate the number of unique words used in the note’s text sorted descending.")
    Map<String, Integer> getNoteStats(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Note id", required = true) @PathVariable String noteId
    ) {
        return noteService.getNoteStats(user.getUsername(), noteId);
    }

    @GetMapping("/compact")
    @Operation(summary = "Get compact notes", description = "The app should allow listing the notes showing only their "
            + "“Title” and “Created Date”.")
    List<CompactNoteDto> getAllCompactNotesByUsername(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            Pageable pageable
    ) {
        return noteService.getAllCompactNotesByUsername(user.getUsername(), pageable);
    }

    @GetMapping("/text/{noteId}")
    @Operation(summary = "Get note text", description = "Returns the text of a note for the authenticated user.")
    NoteTextDto getNoteText(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Note id", required = true) @PathVariable String noteId
    ) {
        return noteService.getNoteText(user.getUsername(), noteId);
    }
}
