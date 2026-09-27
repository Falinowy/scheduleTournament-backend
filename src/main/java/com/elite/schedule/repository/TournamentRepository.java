package com.elite.schedule.repository;

import com.elite.schedule.model.Tournament;
import com.elite.schedule.model.TournamentWithKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.elite.schedule.repository.FirebaseHelper.*;

@Repository
public class TournamentRepository {

    private static final String TOURNAMENTS_PATH = "tournaments";

    private DatabaseReference getRef() {
        return FirebaseDatabase.getInstance().getReference(TOURNAMENTS_PATH);
    }

    public List<TournamentWithKey> findAll() {
        return execute(() -> {
            DataSnapshot snapshot = readSnapshot(getRef());
            List<TournamentWithKey> tournaments = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                tournaments.add(snapshotToRecord(child));
            }
            return tournaments;
        }, "read tournaments from Firebase");
    }

    public TournamentWithKey findByTourneyId(String tourneyId) {
        return execute(() -> {
            DataSnapshot snapshot = readSnapshot(getRef().orderByChild("id").equalTo(tourneyId));
            for (DataSnapshot child : snapshot.getChildren()) {
                return snapshotToRecord(child);
            }
            return null;
        }, "find tournament " + tourneyId);
    }

    /**
     * Looks up the Firebase push-key for a given domain tournament ID.
     * Returns null if no tournament with that ID exists.
     */
    public String findFirebaseKeyByTourneyId(String tourneyId) {
        TournamentWithKey found = findByTourneyId(tourneyId);
        return found != null ? found.key() : null;
    }

    public String save(Tournament tournament) {
        return execute(() -> {
            DatabaseReference newRef = getRef().push();
            Map<String, Object> data = Map.of("id", tournament.getId(), "name", tournament.getName());
            newRef.setValueAsync(data).get();
            return newRef.getKey();
        }, "save tournament");
    }

    public void update(String firebaseKey, Tournament tournament) {
        executeVoid(() -> {
            Map<String, Object> data = Map.of("id", tournament.getId(), "name", tournament.getName());
            getRef().child(firebaseKey).updateChildrenAsync(data).get();
        }, "update tournament");
    }

    public void deleteByKey(String firebaseKey) {
        executeVoid(
                () -> getRef().child(firebaseKey).removeValueAsync().get(),
                "delete tournament"
        );
    }

    private TournamentWithKey snapshotToRecord(DataSnapshot child) {
        return new TournamentWithKey(
                child.getKey(),
                stringValue(child, "id"),
                stringValue(child, "name")
        );
    }
}
