package com.elite.schedule.repository;

import com.elite.schedule.model.Game;
import com.elite.schedule.model.Standings;
import com.elite.schedule.model.Team;
import com.elite.schedule.model.Tournament;
import com.elite.schedule.model.TourneyData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.elite.schedule.repository.FirebaseHelper.*;
import static com.elite.schedule.repository.SnapshotParser.*;

/**
 * Orchestrates all data operations for a single tournament's node
 * (tournaments-data/{tourneyId}) in Firebase.
 */
@Repository
public class TourneyDataRepository {

    private static final String TOURNEY_DATA_PATH = "tournaments-data";

    // --- Nested list repositories ---

    private final FirebaseListRepository<Team, Integer> teams = new FirebaseListRepository<>() {
        @Override protected DatabaseReference collectionRef(String tourneyId) { return dataRef(tourneyId).child("teams"); }
        @Override protected List<Team> parseAll(DataSnapshot snap)           { return parseTeams(snap); }
        @Override protected java.util.function.Predicate<Team> matchesId(Integer id) { return t -> t.getId() == id; }
        @Override protected String entityName()                               { return "team"; }
    };

    private final FirebaseListRepository<Game, Integer> games = new FirebaseListRepository<>() {
        @Override protected DatabaseReference collectionRef(String tourneyId) { return dataRef(tourneyId).child("games"); }
        @Override protected List<Game> parseAll(DataSnapshot snap)           { return parseGames(snap); }
        @Override protected java.util.function.Predicate<Game> matchesId(Integer id) { return g -> g.getId() == id; }
        @Override protected String entityName()                               { return "game"; }
    };

    // --- Full tournament data ---

    public TourneyData findByTourneyId(String tourneyId) {
        return execute(() -> {
            DataSnapshot snapshot = readSnapshot(dataRef(tourneyId));
            if (!snapshot.exists()) return null;
            return new TourneyData(
                    parseTournament(snapshot.child("tournament")),
                    parseTeams(snapshot.child("teams")),
                    parseGames(snapshot.child("games")),
                    parseStandings(snapshot.child("standings")),
                    parseLocations(snapshot.child("locations"))
            );
        }, "read tourney data for " + tourneyId);
    }

    public void initializeData(String tourneyId, Tournament tournament) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tournament", Map.of("id", tournament.getId(), "name", tournament.getName()));
        data.put("teams",     new ArrayList<>());
        data.put("games",     new ArrayList<>());
        data.put("standings", new ArrayList<>());
        data.put("locations", new ArrayList<>());
        executeVoid(() -> dataRef(tourneyId).setValueAsync(data).get(),
                    "initialize tourney data for " + tourneyId);
    }

    public void deleteData(String tourneyId) {
        executeVoid(() -> dataRef(tourneyId).removeValueAsync().get(),
                    "delete tourney data for " + tourneyId);
    }

    // --- Teams ---

    public List<Team> findTeams(String tourneyId)                       { return teams.findAll(tourneyId); }
    public void addTeam(String tourneyId, Team team)                    { teams.add(tourneyId, team); }
    public void updateTeam(String tourneyId, int teamId, Team team)     { teams.updateById(tourneyId, teamId, team); }
    public void deleteTeam(String tourneyId, int teamId)                { teams.deleteById(tourneyId, teamId); }

    // --- Games ---

    public List<Game> findGames(String tourneyId)                       { return games.findAll(tourneyId); }
    public void addGame(String tourneyId, Game game)                    { games.add(tourneyId, game); }
    public void updateGame(String tourneyId, int gameId, Game game)     { games.updateById(tourneyId, gameId, game); }
    public void deleteGame(String tourneyId, int gameId)                { games.deleteById(tourneyId, gameId); }

    // --- Standings ---

    public List<Standings> findStandings(String tourneyId) {
        return execute(
                () -> parseStandings(readSnapshot(dataRef(tourneyId).child("standings"))),
                "read standings"
        );
    }

    public void updateStandings(String tourneyId, List<Standings> standings) {
        executeVoid(() -> dataRef(tourneyId).child("standings").setValueAsync(standings).get(),
                    "update standings");
    }

    // --- Private helpers ---

    private DatabaseReference dataRef(String tourneyId) {
        return FirebaseDatabase.getInstance().getReference(TOURNEY_DATA_PATH).child(tourneyId);
    }
}
