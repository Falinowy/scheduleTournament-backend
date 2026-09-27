package com.elite.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Standings {
    private String division;
    private int losses;
    private int pointsAgainst;
    private int pointsDiff;
    private int pointsFor;
    private int teamId;
    private String teamName;
    private String winningPct;
    private int wins;
}
