package com.elite.schedule.model;

public record Standings(
        String division,
        int losses,
        int pointsAgainst,
        int pointsDiff,
        int pointsFor,
        int teamId,
        String teamName,
        String winningPct,
        int wins
) {
}
