package com.elite.schedule.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourneyData {
    private Tournament tournament;
    private List<Team> teams;
    private List<Game> games;
    private List<Standings> standings;
    private List<Location> locations;
}
