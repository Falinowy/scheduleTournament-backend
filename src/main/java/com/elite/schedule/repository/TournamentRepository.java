package com.elite.schedule.repository;

import com.elite.schedule.model.Tournament;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class TournamentRepository {

    private static final String TOURNAMENTS_PATH = "tournaments";

    private DatabaseReference getRef() {
        return FirebaseDatabase.getInstance().getReference(TOURNAMENTS_PATH);
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

    public List<Map<String, Object>> findAll() {
        try {
            DataSnapshot snapshot = readSnapshot(getRef());

            List<Map<String, Object>> tournaments = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Map<String, Object> tournament = new HashMap<>();
                tournament.put("key", child.getKey());
                tournament.put("id", child.child("id").getValue(String.class));
                tournament.put("name", child.child("name").getValue(String.class));
                tournaments.add(tournament);
            }
            return tournaments;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read tournaments from Firebase", e);
        }
    }

    public Map<String, Object> findByTourneyId(String tourneyId) {
        try {
            DataSnapshot snapshot = readSnapshot(getRef().orderByChild("id").equalTo(tourneyId));

            for (DataSnapshot child : snapshot.getChildren()) {
                Map<String, Object> tournament = new HashMap<>();
                tournament.put("key", child.getKey());
                tournament.put("id", child.child("id").getValue(String.class));
                tournament.put("name", child.child("name").getValue(String.class));
                return tournament;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find tournament " + tourneyId, e);
        }
    }

    public String save(Tournament tournament) {
        try {
            DatabaseReference newRef = getRef().push();
            Map<String, Object> data = Map.of("id", tournament.id(), "name", tournament.name());
            newRef.setValueAsync(data).get();
            return newRef.getKey();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save tournament", e);
        }
    }

    public void update(String firebaseKey, Tournament tournament) {
        try {
            Map<String, Object> data = Map.of("id", tournament.id(), "name", tournament.name());
            getRef().child(firebaseKey).updateChildrenAsync(data).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update tournament", e);
        }
    }

    public void deleteByKey(String firebaseKey) {
        try {
            getRef().child(firebaseKey).removeValueAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete tournament", e);
        }
    }
}
