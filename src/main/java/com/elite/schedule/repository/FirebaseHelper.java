package com.elite.schedule.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Low-level Firebase utility methods: async snapshot reading,
 * typed value extraction, and safe execution wrappers.
 */
public final class FirebaseHelper {

    private FirebaseHelper() {}

    // --- Safe execution wrappers ---

    @FunctionalInterface
    public interface FirebaseSupplier<T> {
        T get() throws InterruptedException, ExecutionException;
    }

    @FunctionalInterface
    public interface FirebaseAction {
        void run() throws InterruptedException, ExecutionException;
    }

    /**
     * Executes a Firebase operation that returns a value,
     * wrapping checked exceptions into a RuntimeException.
     */
    public static <T> T execute(FirebaseSupplier<T> supplier, String description) {
        try {
            return supplier.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to " + description, e);
        }
    }

    /**
     * Executes a Firebase void operation,
     * wrapping checked exceptions into a RuntimeException.
     */
    public static void executeVoid(FirebaseAction action, String description) {
        try {
            action.run();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to " + description, e);
        }
    }

    // --- Async read ---

    public static DataSnapshot readSnapshot(Query query) {
        return execute(
                () -> {
                    CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            future.complete(snapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(error.toException());
                        }
                    });
                    return future.get();
                },
                "read snapshot from Firebase"
        );
    }

    // --- Typed value extraction ---

    public static String stringValue(DataSnapshot snap, String field) {
        Object val = snap.child(field).getValue();
        return val != null ? String.valueOf(val) : null;
    }

    public static int intValue(DataSnapshot snap, String field) {
        Object val = snap.child(field).getValue();
        if (val instanceof Number number) {
            return number.intValue();
        }
        if (val instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    public static double doubleValue(DataSnapshot snap, String field) {
        Object val = snap.child(field).getValue();
        if (val instanceof Number number) {
            return number.doubleValue();
        }
        if (val instanceof String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ignored) {}
        }
        return 0.0;
    }
}
