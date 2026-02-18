package org.example.notes_app.service;

import org.example.notes_app.dto.*;
import org.example.notes_app.entity.Note;
import org.example.notes_app.exception.EntityNotFoundException;
import org.example.notes_app.mapper.NoteMapper;
import org.example.notes_app.repository.NoteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public ResponseNoteDto createNote(String username, CreateNoteDto createNoteDto) {
        Note note = noteMapper.toEntity(createNoteDto);
        note.setUsername(username);
        note.setCreatedDateTime(java.time.LocalDateTime.now());

        return noteMapper.toDto(noteRepository.save(note));
    }

    public ResponseNoteDto updateNote(String username, String noteId, UpdateNoteDto updateNoteDto) {
        Note note = getNoteById(username, noteId);
        note.setTitle(isNullOrEmpty(updateNoteDto.title()) ? note.getTitle() : updateNoteDto.title());
        note.setText(isNullOrEmpty(updateNoteDto.text()) ? note.getText() : updateNoteDto.text());
        note.setTags(isNullOrEmpty(updateNoteDto.tags()) ? note.getTags() : updateNoteDto.tags());

        return noteMapper.toDto(noteRepository.save(note));
    }

    public void deleteNote(String username, String noteId) {
        Note note = getNoteById(username, noteId);
        noteRepository.delete(note);
    }

    public List<ResponseNoteDto> getAllNotesByUsername(String username, FilterDto filterDto, Pageable pageable) {
        List<Note> notes = filterNotesIfNeeded(filterDto, username, pageable);

        return noteMapper.toDtoList(sortNotesByDate(notes));
    }

    public List<CompactNoteDto> getAllCompactNotesByUsername(String username, Pageable pageable) {
        return noteMapper.toCompactDtoList(noteRepository.findAllByUsername(username, pageable));
    }

    public NoteTextDto getNoteText(String username, String noteId) {
        return noteMapper.toNoteTextDto(getNoteById(username, noteId));
    }

    public Map<String, Integer> getNoteStats(String username, String noteId) {
        Note note = getNoteById(username, noteId);
        String[] noteWords = replaceNonWordChars(note.getText()).split(" ");
        Map<String, Integer> stats = new TreeMap<>(Comparator.reverseOrder());
        collectWordsIntoMap(stats, noteWords);

        return stats;
    }

    private Note getNoteById(String username, String noteId) {
        return noteRepository.findByUsernameAndId(username, noteId).orElseThrow(
                () -> new EntityNotFoundException("Note with id: " + noteId + " not found")
        );
    }

    private List<Note> filterNotesIfNeeded(FilterDto filterDto, String username, Pageable pageable) {
        if (filterDto == null || filterDto.tags() == null || filterDto.tags().isEmpty()) {
            return noteRepository.findAllByUsername(username, pageable);
        } else {
            return noteRepository.findAllByUsernameAndTagsContainsAny(
                    username,
                    filterDto.tags(),
                    pageable
            );
        }
    }

    private List<Note> sortNotesByDate(List<Note> notes) {
        return notes.stream().sorted(Comparator.comparing(Note::getCreatedDateTime).reversed()).toList();
    }

    private String replaceNonWordChars(String text) {
        return text.replaceAll("[^a-zA-Z ]", "");
    }

    private void collectWordsIntoMap(Map<String, Integer> stats, String[] words) {
        for (String word : words) {
            stats.put(word, stats.getOrDefault(word, 0) + 1);
        }
    }

    private boolean isNullOrEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof String) return ((String) obj).isEmpty();
        return false;
    }
}
