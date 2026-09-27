package com.elite.schedule.service;

import com.elite.schedule.exception.ResourceNotFoundException;
import com.elite.schedule.model.Game;
import com.elite.schedule.model.Standings;
import com.elite.schedule.model.Team;
import com.elite.schedule.model.TourneyData;
import com.elite.schedule.repository.TourneyDataRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourneyDataService {

    private final TourneyDataRepository repository;

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

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private void validateTeamFields(Team team) {
        requireNotBlank(team.getName(), "Team name");
        requireNotBlank(team.getCoach(), "Team coach");
        requireNotBlank(team.getDivision(), "Team division");
    }

    public void addTeam(String tourneyId, Team team) {
        validateTeamFields(team);

        List<Team> existingTeams = repository.findTeams(tourneyId);
        if (existingTeams.stream().anyMatch(t -> t.getId() == team.getId())) {
            throw new IllegalArgumentException("Team with ID " + team.getId() + " already exists.");
        }

        repository.addTeam(tourneyId, team);
    }

    public void updateTeam(String tourneyId, int teamId, Team team) {
        validateTeamFields(team);

        List<Team> existingTeams = repository.findTeams(tourneyId);
        boolean exists = existingTeams.stream().anyMatch(t -> t.getId() == teamId);
        if (!exists) {
            throw new ResourceNotFoundException("Team with ID " + teamId + " not found.");
        }

        if (teamId != team.getId()) {
            boolean newIdExists = existingTeams.stream().anyMatch(t -> t.getId() == team.getId());
            if (newIdExists) {
                throw new IllegalArgumentException("Cannot change ID to " + team.getId() + " because it already exists.");
            }
        }

        repository.updateTeam(tourneyId, teamId, team);
    }

    public void deleteTeam(String tourneyId, int teamId) {
        List<Game> games = repository.findGames(tourneyId);
        boolean isReferenced = games.stream().anyMatch(g -> g.getTeam1Id() == teamId || g.getTeam2Id() == teamId);
        if (isReferenced) {
            throw new IllegalArgumentException("Cannot delete team with ID " + teamId + " because it is referenced in scheduled games.");
        }
        repository.deleteTeam(tourneyId, teamId);
    }

    // --- Games ---

    public List<Game> getGames(String tourneyId) {
        return repository.findGames(tourneyId);
    }

    private void validateGame(String tourneyId, Game game) {
        if (game.getTeam1Id() == game.getTeam2Id()) {
            throw new IllegalArgumentException("A team cannot play against itself.");
        }
        List<Team> teams = repository.findTeams(tourneyId);
        boolean team1Exists = teams.stream().anyMatch(t -> t.getId() == game.getTeam1Id());
        boolean team2Exists = teams.stream().anyMatch(t -> t.getId() == game.getTeam2Id());
        if (!team1Exists) {
            throw new IllegalArgumentException("Team 1 with ID " + game.getTeam1Id() + " does not exist.");
        }
        if (!team2Exists) {
            throw new IllegalArgumentException("Team 2 with ID " + game.getTeam2Id() + " does not exist.");
        }
    }

    public void addGame(String tourneyId, Game game) {
        validateGame(tourneyId, game);
        repository.addGame(tourneyId, game);
    }

    public void updateGame(String tourneyId, int gameId, Game game) {
        validateGame(tourneyId, game);
        List<Game> existingGames = repository.findGames(tourneyId);
        boolean exists = existingGames.stream().anyMatch(g -> g.getId() == gameId);
        if (!exists) {
            throw new ResourceNotFoundException("Game with ID " + gameId + " not found.");
        }
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
