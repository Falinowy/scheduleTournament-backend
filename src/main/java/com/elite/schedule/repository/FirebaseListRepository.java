package com.elite.schedule.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.function.Predicate;

import static com.elite.schedule.repository.FirebaseHelper.*;

/**
 * Generic repository base providing find/add/update/delete
 * list operations against a single Firebase child node.
 *
 * @param <T>  domain entity type
 * @param <ID> type of the entity's identifier
 */
public abstract class FirebaseListRepository<T, ID> {

    protected abstract DatabaseReference collectionRef(String tourneyId);
    protected abstract List<T> parseAll(DataSnapshot snap);
    protected abstract Predicate<T> matchesId(ID id);
    protected abstract String entityName();

    public List<T> findAll(String tourneyId) {
        return execute(
                () -> parseAll(readSnapshot(collectionRef(tourneyId))),
                "read " + entityName() + "s"
        );
    }

    public void add(String tourneyId, T entity) {
        List<T> items = findAll(tourneyId);
        items.add(entity);
        persist(tourneyId, items, "add");
    }

    public void updateById(String tourneyId, ID id, T updated) {
        List<T> items = findAll(tourneyId);
        for (int i = 0; i < items.size(); i++) {
            if (matchesId(id).test(items.get(i))) {
                items.set(i, updated);
                break;
            }
        }
        persist(tourneyId, items, "update");
    }

    public void deleteById(String tourneyId, ID id) {
        List<T> items = findAll(tourneyId);
        items.removeIf(matchesId(id));
        persist(tourneyId, items, "delete");
    }

    private void persist(String tourneyId, List<T> items, String operation) {
        executeVoid(
                () -> collectionRef(tourneyId).setValueAsync(items).get(),
                operation + " " + entityName()
        );
    }
}
