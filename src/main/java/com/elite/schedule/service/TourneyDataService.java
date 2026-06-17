package com.elite.schedule.service;

import com.elite.schedule.exception.ResourceNotFoundException;
import com.elite.schedule.model.*;
import com.elite.schedule.repository.TourneyDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourneyDataService {

    private final TourneyDataRepository repository;

    public TourneyDataService(TourneyDataRepository repository) {
        this.repository = repository;
    }

    // --- Full data ---

    public TourneyData getTourneyData(String tourneyId) {
        TourneyData data = repository.findByTourneyId(tourneyId);
        if (data == null) {
            throw new ResourceNotFoundException("Tournament data not found: " + tourneyId);
        }
        return data;
    }

    // --- Teams ---

    public List<Team> getTeams(String tourneyId) {
        return repository.findTeams(tourneyId);
    }

    public void addTeam(String tourneyId, Team team) {
        if (team.name() == null || team.name().isBlank()) {
            throw new IllegalArgumentException("Team name is required");
        }
        repository.addTeam(tourneyId, team);
    }

    public void updateTeam(String tourneyId, int teamId, Team team) {
        repository.updateTeam(tourneyId, teamId, team);
    }

    public void deleteTeam(String tourneyId, int teamId) {
        repository.deleteTeam(tourneyId, teamId);
    }

    // --- Games ---

    public List<Game> getGames(String tourneyId) {
        return repository.findGames(tourneyId);
    }

    public void addGame(String tourneyId, Game game) {
        repository.addGame(tourneyId, game);
    }

    public void updateGame(String tourneyId, int gameId, Game game) {
        repository.updateGame(tourneyId, gameId, game);
    }

    public void deleteGame(String tourneyId, int gameId) {
        repository.deleteGame(tourneyId, gameId);
    }

    // --- Standings ---

    public List<Standings> getStandings(String tourneyId) {
        return repository.findStandings(tourneyId);
    }

    public void updateStandings(String tourneyId, List<Standings> standings) {
        repository.updateStandings(tourneyId, standings);
    }
}
