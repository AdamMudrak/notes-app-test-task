package org.example.notes_app.repository;

import org.example.notes_app.entity.Note;
import org.example.notes_app.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    @Query("{ 'username': ?0, 'tags': { $in: ?1 } }")
    List<Note> findAllByUsernameAndTagsContainsAny(String username, Set<Tag> tags, Pageable pageable);

    List<Note> findAllByUsername(String username, Pageable pageable);

    Optional<Note> findByUsernameAndId(String username, String id);
}
