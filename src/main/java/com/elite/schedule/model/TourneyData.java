package com.elite.schedule.model;

import java.util.List;

public record TourneyData(
        Tournament tournament,
        List<Team> teams,
        List<Game> games,
        List<Standings> standings,
        List<Location> locations
) {
}
