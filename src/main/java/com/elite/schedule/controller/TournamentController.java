package com.elite.schedule.controller;

import com.elite.schedule.model.Tournament;
import com.elite.schedule.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<Map<String, Object>> getAll() {
        return service.getAllTournaments();
    }

    @GetMapping("/{tourneyId}")
    public Map<String, Object> getById(@PathVariable String tourneyId) {
        return service.getTournamentById(tourneyId);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Tournament tournament) {
        String firebaseKey = service.createTournament(tournament);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("key", firebaseKey, "id", tournament.id()));
    }

    @PutMapping("/{firebaseKey}")
    public ResponseEntity<Void> update(
            @PathVariable String firebaseKey,
            @RequestBody Tournament tournament
    ) {
        service.updateTournament(firebaseKey, tournament);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{firebaseKey}")
    public ResponseEntity<Void> delete(@PathVariable String firebaseKey) {
        service.deleteTournament(firebaseKey);
        return ResponseEntity.noContent().build();
    }
}
