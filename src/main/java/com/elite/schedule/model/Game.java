package com.elite.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private int id;
    private String locationId;
    private String team1;
    private int team1Id;
    private String team1Score;
    private String team2;
    private int team2Id;
    private String team2Score;
    private String location;
    private String locationUrl;
    private String time;
}
