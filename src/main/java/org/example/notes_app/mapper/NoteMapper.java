package org.example.notes_app.mapper;

import org.example.notes_app.dto.CompactNoteDto;
import org.example.notes_app.dto.CreateNoteDto;
import org.example.notes_app.dto.NoteTextDto;
import org.example.notes_app.dto.ResponseNoteDto;
import org.example.notes_app.entity.Note;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toEntity(CreateNoteDto createNoteDto);
    ResponseNoteDto toDto(Note note);
    List<ResponseNoteDto> toDtoList(List<Note> notes);
    List<CompactNoteDto> toCompactDtoList(List<Note> notes);
    NoteTextDto toNoteTextDto(Note note);
}
