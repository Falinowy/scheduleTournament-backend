package com.elite.schedule.repository;

import com.elite.schedule.model.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class TourneyDataRepository {

    private static final String TOURNEY_DATA_PATH = "tournaments-data";

    private DatabaseReference getRef(String tourneyId) {
        return FirebaseDatabase.getInstance().getReference(TOURNEY_DATA_PATH).child(tourneyId);
    }

    private DataSnapshot readSnapshot(com.google.firebase.database.Query query) throws InterruptedException, ExecutionException {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future.get();
    }

    // --- Full tournament data ---

    public TourneyData findByTourneyId(String tourneyId) {
        try {
            DataSnapshot snapshot = readSnapshot(getRef(tourneyId));
            if (!snapshot.exists()) {
                return null;
            }

            Tournament tournament = parseTournament(snapshot.child("tournament"));
            List<Team> teams = parseTeams(snapshot.child("teams"));
            List<Game> games = parseGames(snapshot.child("games"));
            List<Standings> standings = parseStandings(snapshot.child("standings"));
            List<Location> locations = parseLocations(snapshot.child("locations"));

            return new TourneyData(tournament, teams, games, standings, locations);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to read tourney data for " + tourneyId, e);
        }
    }

    // --- Teams ---

    public List<Team> findTeams(String tourneyId) {
        try {
            DataSnapshot snapshot = readSnapshot(getRef(tourneyId).child("teams"));
            return parseTeams(snapshot);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to read teams", e);
        }
    }

    public void addTeam(String tourneyId, Team team) {
        try {
            List<Team> teams = findTeams(tourneyId);
            teams.add(team);
            getRef(tourneyId).child("teams").setValueAsync(teams).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to add team", e);
        }
    }

    public void updateTeam(String tourneyId, int teamId, Team team) {
        try {
            List<Team> teams = findTeams(tourneyId);
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).id() == teamId) {
                    teams.set(i, team);
                    break;
                }
            }
            getRef(tourneyId).child("teams").setValueAsync(teams).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update team", e);
        }
    }

    public void deleteTeam(String tourneyId, int teamId) {
        try {
            List<Team> teams = findTeams(tourneyId);
            teams.removeIf(t -> t.id() == teamId);
            getRef(tourneyId).child("teams").setValueAsync(teams).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete team", e);
        }
    }

    // --- Games ---

    public List<Game> findGames(String tourneyId) {
        try {
            DataSnapshot snapshot = readSnapshot(getRef(tourneyId).child("games"));
            return parseGames(snapshot);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to read games", e);
        }
    }

    public void addGame(String tourneyId, Game game) {
        try {
            List<Game> games = findGames(tourneyId);
            games.add(game);
            getRef(tourneyId).child("games").setValueAsync(games).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to add game", e);
        }
    }

    public void updateGame(String tourneyId, int gameId, Game game) {
        try {
            List<Game> games = findGames(tourneyId);
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).id() == gameId) {
                    games.set(i, game);
                    break;
                }
            }
            getRef(tourneyId).child("games").setValueAsync(games).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update game", e);
        }
    }

    public void deleteGame(String tourneyId, int gameId) {
        try {
            List<Game> games = findGames(tourneyId);
            games.removeIf(g -> g.id() == gameId);
            getRef(tourneyId).child("games").setValueAsync(games).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete game", e);
        }
    }

    // --- Standings ---

    public List<Standings> findStandings(String tourneyId) {
        try {
            DataSnapshot snapshot = readSnapshot(getRef(tourneyId).child("standings"));
            return parseStandings(snapshot);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to read standings", e);
        }
    }

    public void updateStandings(String tourneyId, List<Standings> standings) {
        try {
            getRef(tourneyId).child("standings").setValueAsync(standings).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update standings", e);
        }
    }

    // --- Parsers ---

    private Tournament parseTournament(DataSnapshot snap) {
        if (!snap.exists()) return null;
        return new Tournament(
                snap.child("id").getValue(String.class),
                snap.child("name").getValue(String.class)
        );
    }

    private List<Team> parseTeams(DataSnapshot snap) {
        List<Team> teams = new ArrayList<>();
        if (!snap.exists()) return teams;
        for (DataSnapshot child : snap.getChildren()) {
            teams.add(new Team(
                    intValue(child, "id"),
                    child.child("name").getValue(String.class),
                    child.child("coach").getValue(String.class),
                    child.child("division").getValue(String.class)
            ));
        }
        return teams;
    }

    private List<Game> parseGames(DataSnapshot snap) {
        List<Game> games = new ArrayList<>();
        if (!snap.exists()) return games;
        for (DataSnapshot child : snap.getChildren()) {
            games.add(new Game(
                    intValue(child, "id"),
                    child.child("locationId").getValue(String.class),
                    child.child("team1").getValue(String.class),
                    intValue(child, "team1Id"),
                    child.child("team1Score").getValue(String.class),
                    child.child("team2").getValue(String.class),
                    intValue(child, "team2Id"),
                    child.child("team2Score").getValue(String.class),
                    child.child("location").getValue(String.class),
                    child.child("locationUrl").getValue(String.class),
                    child.child("time").getValue(String.class)
            ));
        }
        return games;
    }

    private List<Standings> parseStandings(DataSnapshot snap) {
        List<Standings> standings = new ArrayList<>();
        if (!snap.exists()) return standings;
        for (DataSnapshot child : snap.getChildren()) {
            standings.add(new Standings(
                    child.child("division").getValue(String.class),
                    intValue(child, "losses"),
                    intValue(child, "pointsAgainst"),
                    intValue(child, "pointsDiff"),
                    intValue(child, "pointsFor"),
                    intValue(child, "teamId"),
                    child.child("teamName").getValue(String.class),
                    child.child("winningPct").getValue(String.class),
                    intValue(child, "wins")
            ));
        }
        return standings;
    }

    private List<Location> parseLocations(DataSnapshot snap) {
        List<Location> locations = new ArrayList<>();
        if (!snap.exists()) return locations;
        for (DataSnapshot child : snap.getChildren()) {
            locations.add(new Location(
                    child.child("name").getValue(String.class),
                    doubleValue(child, "latitude"),
                    doubleValue(child, "longitude")
            ));
        }
        return locations;
    }

    private int intValue(DataSnapshot snap, String field) {
        Long val = snap.child(field).getValue(Long.class);
        return val != null ? val.intValue() : 0;
    }

    private double doubleValue(DataSnapshot snap, String field) {
        Double val = snap.child(field).getValue(Double.class);
        return val != null ? val : 0.0;
    }
}
