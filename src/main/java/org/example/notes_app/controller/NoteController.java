package org.example.notes_app.controller;

import jakarta.validation.Valid;
import org.example.notes_app.dto.*;
import org.example.notes_app.service.NoteService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseNoteDto createNote(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateNoteDto createNoteDto
    ) {
        return noteService.createNote(user.getUsername(), createNoteDto);
    }

    @PutMapping("/{noteId}")
    public ResponseNoteDto updateNote(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String noteId,
            @RequestBody UpdateNoteDto updateNoteDto
    ) {
        return noteService.updateNote(user.getUsername(), noteId, updateNoteDto);
    }

    @DeleteMapping("/{noteId}")
    public void deleteNote(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String noteId
    ) {
        noteService.deleteNote(user.getUsername(), noteId);
    }

    @GetMapping
    public List<ResponseNoteDto> getAllNotesByUsername(
            @AuthenticationPrincipal UserDetails user,
            FilterDto filterDto,
            @PageableDefault(sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return noteService.getAllNotesByUsername(user.getUsername(), filterDto, pageable);
    }

    @GetMapping("/{noteId}/stats")
    public Map<String, Integer> getNoteStats(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String noteId
    ) {
        return noteService.getNoteStats(user.getUsername(), noteId);
    }

    @GetMapping("/compact")
    public List<CompactNoteDto> getAllCompactNotesByUsername(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return noteService.getAllCompactNotesByUsername(user.getUsername(), pageable);
    }

    @GetMapping("/text/{noteId}")
    public NoteTextDto getNoteText(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String noteId
    ) {
        return noteService.getNoteText(user.getUsername(), noteId);
    }
}
