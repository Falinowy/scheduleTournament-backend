package com.elite.schedule.service;

import com.elite.schedule.exception.ResourceNotFoundException;
import com.elite.schedule.model.Tournament;
import com.elite.schedule.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TournamentService {

    private final TournamentRepository repository;

    public TournamentService(TournamentRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> getAllTournaments() {
        return repository.findAll();
    }

    public Map<String, Object> getTournamentById(String tourneyId) {
        Map<String, Object> tournament = repository.findByTourneyId(tourneyId);
        if (tournament == null) {
            throw new ResourceNotFoundException("Tournament not found: " + tourneyId);
        }
        return tournament;
    }

    public String createTournament(Tournament tournament) {
        if (tournament.id() == null || tournament.id().isBlank()) {
            throw new IllegalArgumentException("Tournament ID is required");
        }
        if (tournament.name() == null || tournament.name().isBlank()) {
            throw new IllegalArgumentException("Tournament name is required");
        }
        return repository.save(tournament);
    }

    public void updateTournament(String firebaseKey, Tournament tournament) {
        repository.update(firebaseKey, tournament);
    }

    public void deleteTournament(String firebaseKey) {
        repository.deleteByKey(firebaseKey);
    }
}
