package com.elite.schedule.controller;

import com.elite.schedule.model.Tournament;
import com.elite.schedule.model.TournamentWithKey;
import com.elite.schedule.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService service;

    public TournamentController(TournamentService service) {
        this.service = service;
    }

    @GetMapping
    public List<TournamentWithKey> getAll() {
        return service.getAllTournaments();
    }

    @GetMapping("/{tourneyId}")
    public TournamentWithKey getById(@PathVariable String tourneyId) {
        return service.getTournamentById(tourneyId);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Tournament tournament) {
        String firebaseKey = service.createTournament(tournament);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("key", firebaseKey, "id", tournament.getId()));
    }

    @PutMapping("/{tourneyId}")
    public ResponseEntity<Void> update(
            @PathVariable String tourneyId,
            @RequestBody Tournament tournament
    ) {
        service.updateTournament(tourneyId, tournament);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tourneyId}")
    public ResponseEntity<Void> delete(@PathVariable String tourneyId) {
        service.deleteTournament(tourneyId);
        return ResponseEntity.noContent().build();
    }
}

