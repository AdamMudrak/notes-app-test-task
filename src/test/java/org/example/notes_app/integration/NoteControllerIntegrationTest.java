package org.example.notes_app.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notes_app.dto.CreateNoteDto;
import org.example.notes_app.dto.UpdateNoteDto;
import org.example.notes_app.entity.Note;
import org.example.notes_app.entity.Tag;
import org.example.notes_app.repository.NoteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class NoteControllerIntegrationTest {

    @Container
    @ServiceConnection
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired NoteRepository noteRepository;

    @BeforeAll
    static void init() {
        mongo.start();
    }

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Nested
    class CreateNote {
        @Test
        @WithMockUser(username = "test")
        void createNote_createsAndPersistsNote_forAuthenticatedUser_tagsOptional() throws Exception {
            CreateNoteDto payload = new CreateNoteDto(
                    "My title",
                    "Some note text",
                    null
            );

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.title").value("My title"))
                    .andExpect(jsonPath("$.text").value("Some note text"))
                    .andExpect(jsonPath("$.createdDateTime").isNotEmpty());

            assertThat(noteRepository.count()).isEqualTo(1);
            Note saved = noteRepository.findAll().getFirst();
            assertThat(saved.getUsername()).isEqualTo("test");
            assertThat(saved.getTitle()).isEqualTo("My title");
            assertThat(saved.getText()).isEqualTo("Some note text");
            assertThat(saved.getCreatedDateTime()).isNotNull();
            assertThat(saved.getTags()).isNull();
        }

        @Test
        @WithMockUser(username = "test")
        void createNote_rejectsMissingTitle_orBlankTitle() throws Exception {
            CreateNoteDto missingTitle = new CreateNoteDto(
                    null,
                    "text",
                    Set.of(Tag.BUSINESS)
            );

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(missingTitle)))
                    .andExpect(status().isBadRequest());

            CreateNoteDto blankTitle = new CreateNoteDto(
                    "   ",
                    "text",
                    Set.of(Tag.BUSINESS)
            );

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(blankTitle)))
                    .andExpect(status().isBadRequest());

            assertThat(noteRepository.count()).isZero();
        }

        @Test
        @WithMockUser(username = "test")
        void createNote_rejectsMissingText_orBlankText() throws Exception {
            CreateNoteDto missingText = new CreateNoteDto(
                    "title",
                    null,
                    Set.of(Tag.BUSINESS)
            );

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(missingText)))
                    .andExpect(status().isBadRequest());

            CreateNoteDto blankText = new CreateNoteDto(
                    "title",
                    "   ",
                    Set.of(Tag.BUSINESS)
            );

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(blankText)))
                    .andExpect(status().isBadRequest());

            assertThat(noteRepository.count()).isZero();
        }

        @Test
        @WithMockUser(username = "test")
        void createNote_rejectsInvalidTagValue() throws Exception {
            String json = """
                {
                  "title": "t",
                  "text": "x",
                  "tags": ["BUSINESS", "NOT_A_TAG"]
                }
                """;

            mockMvc.perform(post("/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            assertThat(noteRepository.count()).isZero();
        }
    }

    @Nested
    class UpdateNote {
        @Test
        @WithMockUser(username = "alice")
        void updateNote_updatesFields_forOwner() throws Exception {
            Note existing = seedNote("alice", "Old", "Old text", Set.of(Tag.PERSONAL),
                    LocalDateTime.now().minusDays(1));

            UpdateNoteDto payload = new UpdateNoteDto(
                    "New title",
                    "New text",
                    Set.of(Tag.BUSINESS, Tag.IMPORTANT)
            );

            mockMvc.perform(put("/notes/{id}", existing.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(existing.getId()))
                    .andExpect(jsonPath("$.title").value("New title"))
                    .andExpect(jsonPath("$.text").value("New text"));

            Note updated = noteRepository.findById(existing.getId()).orElseThrow();
            assertThat(updated.getTitle()).isEqualTo("New title");
            assertThat(updated.getText()).isEqualTo("New text");
            assertThat(updated.getTags()).containsExactlyInAnyOrder(Tag.BUSINESS, Tag.IMPORTANT);
        }

        @Test
        @WithMockUser(username = "bob")
        void updateNote_returnsNotFound_forNonOwner() throws Exception {
            Note existing = seedNote("alice", "T", "Text", Set.of(Tag.BUSINESS),
                    LocalDateTime.now().minusDays(1));

            UpdateNoteDto payload = new UpdateNoteDto("X", "Y", Set.of(Tag.PERSONAL));

            mockMvc.perform(put("/notes/{id}", existing.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isNotFound());

            Note still = noteRepository.findById(existing.getId()).orElseThrow();
            assertThat(still.getTitle()).isEqualTo("T");
            assertThat(still.getText()).isEqualTo("Text");
        }
    }

    @Nested
    class DeleteNote {
        @Test
        @WithMockUser(username = "alice")
        void deleteNote_deletes_forOwner() throws Exception {
            Note existing = seedNote("alice", "T", "Text", Set.of(Tag.BUSINESS),
                    LocalDateTime.now().minusHours(2));

            mockMvc.perform(delete("/notes/{id}", existing.getId()))
                    .andExpect(status().isOk());

            assertThat(noteRepository.findById(existing.getId())).isEmpty();
        }

        @Test
        @WithMockUser(username = "bob")
        void deleteNote_returnsNotFound_forNonOwner() throws Exception {
            Note existing = seedNote("alice", "T", "Text", Set.of(Tag.BUSINESS),
                    LocalDateTime.now().minusHours(2));

            mockMvc.perform(delete("/notes/{id}", existing.getId()))
                    .andExpect(status().isNotFound());

            assertThat(noteRepository.findById(existing.getId())).isPresent();
        }
    }

    @Nested
    class GetNote {
        @Test
        @WithMockUser(username = "alice")
        void listNotes_returnsOnlyUserNotes_newestFirst_andPaginates() throws Exception {
            Note n1 = seedNote("alice", "Old", "t1", Set.of(Tag.BUSINESS), LocalDateTime.now().minusDays(2));
            Note n2 = seedNote("alice", "Mid", "t2", Set.of(Tag.PERSONAL), LocalDateTime.now().minusDays(1));
            Note n3 = seedNote("alice", "New", "t3", Set.of(Tag.IMPORTANT), LocalDateTime.now().minusHours(1));

            seedNote("bob", "BobNote", "bbb", Set.of(Tag.BUSINESS), LocalDateTime.now().minusMinutes(10));

            String page0 = mockMvc.perform(get("/notes")
                            .param("page", "0")
                            .param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andReturn().getResponse().getContentAsString();

            var list0 = objectMapper.readTree(page0);
            assertThat(list0.get(0).get("id").asText()).isEqualTo(n3.getId());
            assertThat(list0.get(1).get("id").asText()).isEqualTo(n2.getId());

            mockMvc.perform(get("/notes")
                            .param("page", "1")
                            .param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(n1.getId()));
        }

        @Test
        @WithMockUser(username = "alice")
        void listNotes_filtersByTags_whenProvided() throws Exception {
            Note business = seedNote("alice", "Biz", "t1", Set.of(Tag.BUSINESS), LocalDateTime.now().minusDays(1));
            seedNote("alice", "Pers", "t2", Set.of(Tag.PERSONAL), LocalDateTime.now().minusHours(2));
            Note both = seedNote("alice", "Both", "t3", Set.of(Tag.BUSINESS, Tag.IMPORTANT), LocalDateTime.now().minusHours(1));

            String json = mockMvc.perform(get("/notes")
                            .param("tags", "BUSINESS")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            var arr = objectMapper.readTree(json);
            List<String> ids = List.of(arr.get(0).get("id").asText(), arr.get(1).get("id").asText());
            assertThat(ids).containsExactly(both.getId(), business.getId());
        }

        @Test
        @WithMockUser(username = "alice")
        void listNotes_whenTagsEmpty_doesNotFilter() throws Exception {
            seedNote("alice", "A", "t1", Set.of(Tag.BUSINESS), LocalDateTime.now().minusDays(1));
            seedNote("alice", "B", "t2", Set.of(Tag.PERSONAL), LocalDateTime.now().minusHours(1));

            mockMvc.perform(get("/notes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }


        @Test
        @WithMockUser(username = "alice")
        void compactList_returnsOnlyTitleAndCreatedDate_paged_andNewestFirst() throws Exception {
            seedNote("alice", "Old", "t1", Set.of(Tag.BUSINESS), LocalDateTime.now().minusDays(2));
            seedNote("alice", "New", "t2", Set.of(Tag.PERSONAL), LocalDateTime.now().minusHours(1));

            String json = mockMvc.perform(get("/notes/compact")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            var arr = objectMapper.readTree(json);
            assertThat(arr.size()).isEqualTo(2);


            assertThat(arr.get(0).get("title").asText()).isEqualTo("New");
            assertThat(arr.get(1).get("title").asText()).isEqualTo("Old");

            assertThat(arr.get(0).has("text")).isFalse();
            assertThat(arr.get(0).has("createdDateTime")).isTrue();
        }

        @Test
        @WithMockUser(username = "alice")
        void getNoteText_returnsTextOnly_forOwner() throws Exception {
            Note n = seedNote("alice", "T", "Hello world", Set.of(Tag.IMPORTANT), LocalDateTime.now().minusMinutes(5));

            mockMvc.perform(get("/notes/text/{id}", n.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.text").value("Hello world"))
                    .andExpect(jsonPath("$.title").doesNotExist());
        }

        @Test
        @WithMockUser(username = "bob")
        void getNoteText_returnsNotFound_forNonOwner() throws Exception {
            Note n = seedNote("alice", "T", "Hello world", Set.of(Tag.IMPORTANT), LocalDateTime.now().minusMinutes(5));

            mockMvc.perform(get("/notes/text/{id}", n.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "alice")
        void getNoteStats_countsWords_caseSensitiveAsImplemented_andSortedByCountDesc() throws Exception {
            Note n = seedNote("alice", "T", "note is just just just a note", null, LocalDateTime.now().minusMinutes(1));

            String json = mockMvc.perform(get("/notes/{id}/stats", n.getId()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            LinkedHashMap<String, Integer> map = objectMapper.readValue(
                    json,
                    new TypeReference<>() {
                    }
            );

            assertThat(map).containsEntry("note", 2)
                    .containsEntry("is", 1)
                    .containsEntry("just", 3)
                    .containsEntry("a", 1);

            assertThat(map.keySet().stream().toList()).startsWith("note");
        }

        @Test
        @WithMockUser(username = "bob")
        void getNoteStats_returnsNotFound_forNonOwner() throws Exception {
            Note n = seedNote("alice", "T", "note is just a note", null, LocalDateTime.now().minusMinutes(1));

            mockMvc.perform(get("/notes/{id}/stats", n.getId()))
                    .andExpect(status().isNotFound());
        }
    }
    private Note seedNote(String username,
                          String title,
                          String text,
                          Set<Tag> tags,
                          LocalDateTime created) {
        Note n = new Note();
        n.setUsername(username);
        n.setTitle(title);
        n.setText(text);
        n.setTags(tags);
        n.setCreatedDateTime(created);
        return noteRepository.save(n);
    }
}
