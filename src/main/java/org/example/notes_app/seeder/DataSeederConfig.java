package org.example.notes_app.seeder;

import lombok.RequiredArgsConstructor;
import org.example.notes_app.entity.Note;
import org.example.notes_app.entity.Tag;
import org.example.notes_app.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

//TODO comment out if seeding is unwanted
@Configuration
@RequiredArgsConstructor
public class DataSeederConfig {

    private final NoteRepository noteRepository;

    @Bean
    CommandLineRunner seedNotes() {
        return args -> {

            // prevent duplicating data every restart
            if (noteRepository.count() > 0) {
                return;
            }

            Note n1 = new Note();
            n1.setTitle("Buy office supplies");
            n1.setText("Need paper, printer ink and envelopes");
            n1.setCreatedDate(LocalDate.now().minusDays(2));
            n1.setUsername("user1");
            n1.setTags(Set.of(Tag.BUSINESS));

            Note n2 = new Note();
            n2.setTitle("Gym plan");
            n2.setText("Leg day and cardio session");
            n2.setCreatedDate(LocalDate.now().minusDays(1));
            n2.setUsername("user1");
            n2.setTags(Set.of(Tag.PERSONAL, Tag.IMPORTANT));

            Note n3 = new Note();
            n3.setTitle("Project deadline");
            n3.setText("Finish backend API and write documentation");
            n3.setCreatedDate(LocalDate.now());
            n3.setUsername("user2");
            n3.setTags(Set.of(Tag.BUSINESS, Tag.IMPORTANT));

            Note n4 = new Note();
            n4.setTitle("Weekend trip");
            n4.setText("Book hotel and pack luggage");
            n4.setCreatedDate(LocalDate.now());
            n4.setUsername("user2");
            n4.setTags(Set.of(Tag.PERSONAL));

            noteRepository.saveAll(List.of(n1, n2, n3, n4));
        };
    }
}
