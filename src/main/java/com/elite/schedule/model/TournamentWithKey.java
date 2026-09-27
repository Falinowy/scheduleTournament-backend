package com.elite.schedule.model;

/**
 * Read-only projection of a Tournament including its Firebase push key.
 * Used as the API response type for tournament listings.
 */
public record TournamentWithKey(String key, String id, String name) {
}
