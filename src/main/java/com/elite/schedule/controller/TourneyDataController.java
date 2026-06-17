package com.elite.schedule.controller;

import com.elite.schedule.model.*;
import com.elite.schedule.service.TourneyDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments/{tourneyId}")
public class TourneyDataController {

    private final TourneyDataService service;

    public TourneyDataController(TourneyDataService service) {
        this.service = service;
    }

    // --- Full tournament data ---

    @GetMapping("/data")
    public TourneyData getData(@PathVariable String tourneyId) {
        return service.getTourneyData(tourneyId);
    }

    // --- Teams ---

    @GetMapping("/teams")
    public List<Team> getTeams(@PathVariable String tourneyId) {
        return service.getTeams(tourneyId);
    }

    @PostMapping("/teams")
    public ResponseEntity<Void> addTeam(
            @PathVariable String tourneyId,
            @RequestBody Team team
    ) {
        service.addTeam(tourneyId, team);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/teams/{teamId}")
    public ResponseEntity<Void> updateTeam(
            @PathVariable String tourneyId,
            @PathVariable int teamId,
            @RequestBody Team team
    ) {
        service.updateTeam(tourneyId, teamId, team);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable String tourneyId,
            @PathVariable int teamId
    ) {
        service.deleteTeam(tourneyId, teamId);
        return ResponseEntity.noContent().build();
    }

    // --- Games ---

    @GetMapping("/games")
    public List<Game> getGames(@PathVariable String tourneyId) {
        return service.getGames(tourneyId);
    }

    @PostMapping("/games")
    public ResponseEntity<Void> addGame(
            @PathVariable String tourneyId,
            @RequestBody Game game
    ) {
        service.addGame(tourneyId, game);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/games/{gameId}")
    public ResponseEntity<Void> updateGame(
            @PathVariable String tourneyId,
            @PathVariable int gameId,
            @RequestBody Game game
    ) {
        service.updateGame(tourneyId, gameId, game);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable String tourneyId,
            @PathVariable int gameId
    ) {
        service.deleteGame(tourneyId, gameId);
        return ResponseEntity.noContent().build();
    }

    // --- Standings ---

    @GetMapping("/standings")
    public List<Standings> getStandings(@PathVariable String tourneyId) {
        return service.getStandings(tourneyId);
    }

    @PutMapping("/standings")
    public ResponseEntity<Void> updateStandings(
            @PathVariable String tourneyId,
            @RequestBody List<Standings> standings
    ) {
        service.updateStandings(tourneyId, standings);
        return ResponseEntity.ok().build();
    }
}
