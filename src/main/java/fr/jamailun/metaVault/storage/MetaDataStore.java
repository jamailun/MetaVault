package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A store for meta-data.
 */
public interface MetaDataStore {

    /**
     * Get the UUID owning the store.
     * @return a non-null UUID. One instance will always have the same.
     */
    @NotNull UUID getOwner();

    /**
     * Create a new transaction.
     * @return a new transaction.
     */
    @NotNull MetaDataTransaction newTransaction();

    /**
     * Test if the data-stores contains this value.
     * @param key the key to fetch.
     * @return a future.
     */
    @NotNull Future<Boolean> hasValue(@NotNull String key);

}
