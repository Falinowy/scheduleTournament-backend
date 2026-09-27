package com.elite.schedule.repository;

import com.elite.schedule.model.Game;
import com.elite.schedule.model.Location;
import com.elite.schedule.model.Standings;
import com.elite.schedule.model.Team;
import com.elite.schedule.model.Tournament;
import com.elite.schedule.model.TourneyData;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.elite.schedule.repository.FirebaseHelper.*;

/**
 * Maps Firebase DataSnapshot children to domain model objects.
 * All methods are stateless and static — a pure transformation layer.
 */
public final class SnapshotParser {

    private SnapshotParser() {}

    public static Tournament parseTournament(DataSnapshot snap) {
        if (!snap.exists()) return null;
        return new Tournament(
                stringValue(snap, "id"),
                stringValue(snap, "name")
        );
    }

    public static List<Team> parseTeams(DataSnapshot snap) {
        List<Team> teams = new ArrayList<>();
        if (!snap.exists()) return teams;
        for (DataSnapshot child : snap.getChildren()) {
            teams.add(new Team(
                    intValue(child, "id"),
                    stringValue(child, "name"),
                    stringValue(child, "coach"),
                    stringValue(child, "division")
            ));
        }
        return teams;
    }

    public static List<Game> parseGames(DataSnapshot snap) {
        List<Game> games = new ArrayList<>();
        if (!snap.exists()) return games;
        for (DataSnapshot child : snap.getChildren()) {
            games.add(new Game(
                    intValue(child, "id"),
                    stringValue(child, "locationId"),
                    stringValue(child, "team1"),
                    intValue(child, "team1Id"),
                    stringValue(child, "team1Score"),
                    stringValue(child, "team2"),
                    intValue(child, "team2Id"),
                    stringValue(child, "team2Score"),
                    stringValue(child, "location"),
                    stringValue(child, "locationUrl"),
                    stringValue(child, "time")
            ));
        }
        return games;
    }

    public static List<Standings> parseStandings(DataSnapshot snap) {
        List<Standings> standings = new ArrayList<>();
        if (!snap.exists()) return standings;
        for (DataSnapshot child : snap.getChildren()) {
            standings.add(new Standings(
                    stringValue(child, "division"),
                    intValue(child, "losses"),
                    intValue(child, "pointsAgainst"),
                    intValue(child, "pointsDiff"),
                    intValue(child, "pointsFor"),
                    intValue(child, "teamId"),
                    stringValue(child, "teamName"),
                    stringValue(child, "winningPct"),
                    intValue(child, "wins")
            ));
        }
        return standings;
    }

    public static List<Location> parseLocations(DataSnapshot snap) {
        List<Location> locations = new ArrayList<>();
        if (!snap.exists()) return locations;
        for (DataSnapshot child : snap.getChildren()) {
            locations.add(new Location(
                    stringValue(child, "name"),
                    doubleValue(child, "latitude"),
                    doubleValue(child, "longitude")
            ));
        }
        return locations;
    }
}
