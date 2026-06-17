package com.elite.schedule.model;

public record Game(
        int id,
        String locationId,
        String team1,
        int team1Id,
        String team1Score,
        String team2,
        int team2Id,
        String team2Score,
        String location,
        String locationUrl,
        String time
) {
}
