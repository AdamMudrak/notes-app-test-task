package org.example.notes_app.config;

import lombok.RequiredArgsConstructor;
import org.example.notes_app.entity.Note;
import org.example.notes_app.entity.Tag;
import org.example.notes_app.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

//TODO comment out if drop-create-seeding is unwanted
@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataSeederConfig {

    private final NoteRepository noteRepository;

    @Bean
    CommandLineRunner seedNotes() {
        return args -> {

            noteRepository.deleteAll();

            Note u1n1 = new Note();
            u1n1.setTitle("Quarterly budget review");
            u1n1.setText("Check department expenses, adjust forecasts and prepare finance summary");
            u1n1.setCreatedDateTime(LocalDateTime.now().minusDays(10));
            u1n1.setUsername("user1");
            u1n1.setTags(Set.of(Tag.BUSINESS, Tag.IMPORTANT));

            Note u1n2 = new Note();
            u1n2.setTitle("Morning workout routine");
            u1n2.setText("Stretching, pushups, 20 minutes running and hydration tracking");
            u1n2.setCreatedDateTime(LocalDateTime.now().minusDays(7));
            u1n2.setUsername("user1");
            u1n2.setTags(Set.of(Tag.PERSONAL));

            Note u1n3 = new Note();
            u1n3.setTitle("Client presentation draft");
            u1n3.setText("Prepare slides about product metrics, uptime, growth numbers and roadmap");
            u1n3.setCreatedDateTime(LocalDateTime.now().minusDays(5));
            u1n3.setUsername("user1");
            u1n3.setTags(Set.of(Tag.BUSINESS));

            Note u1n4 = new Note();
            u1n4.setTitle("Health check reminders");
            u1n4.setText("Schedule dentist visit, yearly blood test and vision check appointment");
            u1n4.setCreatedDateTime(LocalDateTime.now().minusDays(3));
            u1n4.setUsername("user1");
            u1n4.setTags(Set.of(Tag.PERSONAL, Tag.IMPORTANT));

            Note u1n5 = new Note();
            u1n5.setTitle("Books to read");
            u1n5.setText("Add distributed systems book, clean architecture guide and productivity notes");
            u1n5.setCreatedDateTime(LocalDateTime.now().minusDays(1));
            u1n5.setUsername("user1");
            u1n5.setTags(Set.of());

            Note u2n1 = new Note();
            u2n1.setTitle("Server migration checklist");
            u2n1.setText("Backup database, verify docker images, run smoke tests and update DNS");
            u2n1.setCreatedDateTime(LocalDateTime.now().minusDays(9));
            u2n1.setUsername("user2");
            u2n1.setTags(Set.of(Tag.BUSINESS, Tag.IMPORTANT));

            Note u2n2 = new Note();
            u2n2.setTitle("Family dinner planning");
            u2n2.setText("Buy vegetables, choose dessert, confirm guests and prepare playlist");
            u2n2.setCreatedDateTime(LocalDateTime.now().minusDays(6));
            u2n2.setUsername("user2");
            u2n2.setTags(Set.of(Tag.PERSONAL));

            Note u2n3 = new Note();
            u2n3.setTitle("Learning goals");
            u2n3.setText("Practice Spring security, review Mongo indexing and study integration testing");
            u2n3.setCreatedDateTime(LocalDateTime.now().minusDays(4));
            u2n3.setUsername("user2");
            u2n3.setTags(Set.of(Tag.IMPORTANT));

            Note u2n4 = new Note();
            u2n4.setTitle("Office relocation tasks");
            u2n4.setText("Contact movers, label hardware, prepare workspace allocation and network plan");
            u2n4.setCreatedDateTime(LocalDateTime.now().minusDays(2));
            u2n4.setUsername("user2");
            u2n4.setTags(Set.of(Tag.BUSINESS));

            Note u2n5 = new Note();
            u2n5.setTitle("Random thoughts");
            u2n5.setText("Ideas about weekend photography, cooking experiments and travel destinations");
            u2n5.setCreatedDateTime(LocalDateTime.now());
            u2n5.setUsername("user2");
            u2n5.setTags(Set.of(Tag.PERSONAL, Tag.BUSINESS, Tag.IMPORTANT));

            noteRepository.saveAll(List.of(
                    u1n1, u1n2, u1n3, u1n4, u1n5,
                    u2n1, u2n2, u2n3, u2n4, u2n5
            ));
        };
    }
}
