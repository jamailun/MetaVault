package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    @NotNull CompletableFuture<Boolean> hasValue(@NotNull String key);

    /**
     * Get stored value.
     * @param key the key to fetch.
     * @return a future.
     */
    @NotNull CompletableFuture<String> getValue(@NotNull String key);

    /**
     * List existing keys.
     * @return a future containing anon-mutable list of keys. Insertion order.
     */
    @NotNull @UnmodifiableView CompletableFuture<List<String>> listKeys();

    /**
     * List existing entries.
     * @return a future containing a non-mutable map of entries.
     */
    @NotNull @UnmodifiableView CompletableFuture<Map<String, String>> getAllEntries();
}
