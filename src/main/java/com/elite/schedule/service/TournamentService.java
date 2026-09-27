package com.elite.schedule.service;

import com.elite.schedule.exception.ResourceNotFoundException;
import com.elite.schedule.model.Tournament;
import com.elite.schedule.model.TournamentWithKey;
import com.elite.schedule.repository.TournamentRepository;
import com.elite.schedule.repository.TourneyDataRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentService {

    private final TournamentRepository repository;
    private final TourneyDataRepository tourneyDataRepository;

    public List<TournamentWithKey> getAllTournaments() {
        return repository.findAll();
    }

    public TournamentWithKey getTournamentById(String tourneyId) {
        TournamentWithKey tournament = repository.findByTourneyId(tourneyId);
        if (tournament == null) {
            throw new ResourceNotFoundException("Tournament not found: " + tourneyId);
        }
        return tournament;
    }

    public String createTournament(Tournament tournament) {
        if (tournament.getId() == null || tournament.getId().isBlank()) {
            throw new IllegalArgumentException("Tournament ID is required");
        }
        if (tournament.getName() == null || tournament.getName().isBlank()) {
            throw new IllegalArgumentException("Tournament name is required");
        }

        List<TournamentWithKey> existingTournaments = repository.findAll();
        boolean exists = existingTournaments.stream()
                .anyMatch(t -> tournament.getId().equals(t.id()));
        if (exists) {
            throw new IllegalArgumentException("Tournament with ID '" + tournament.getId() + "' already exists.");
        }

        String key = repository.save(tournament);
        tourneyDataRepository.initializeData(tournament.getId(), tournament);
        return key;
    }

    public void updateTournament(String tourneyId, Tournament tournament) {
        String firebaseKey = requireFirebaseKey(tourneyId);
        repository.update(firebaseKey, tournament);
    }

    public void deleteTournament(String tourneyId) {
        String firebaseKey = requireFirebaseKey(tourneyId);

        try {
            tourneyDataRepository.deleteData(tourneyId);
        } catch (Exception e) {
            log.error("Failed to delete tournament data for tourneyId: {}", tourneyId, e);
            throw new RuntimeException("Failed to delete tournament data", e);
        }

        repository.deleteByKey(firebaseKey);
        log.info("Deleted tournament '{}' (firebaseKey={})", tourneyId, firebaseKey);
    }

    /**
     * Resolves the Firebase push-key for a domain tournament ID,
     * throwing ResourceNotFoundException if the tournament doesn't exist.
     */
    private String requireFirebaseKey(String tourneyId) {
        String firebaseKey = repository.findFirebaseKeyByTourneyId(tourneyId);
        if (firebaseKey == null) {
            throw new ResourceNotFoundException("Tournament not found: " + tourneyId);
        }
        return firebaseKey;
    }
}

