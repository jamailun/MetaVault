package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * A transaction to the meta-data store.
 */
public interface MetaDataTransaction {

    /**
     * Add a callback. Will only be run after the transaction has been applied AND is a success.
     * @param callback a non-null runnable action.
     * @return this.
     */
    @NotNull MetaDataTransaction addCallback(@NotNull Runnable callback);

    /**
     * Apply changes of the transaction.
     * @return a new future. This will do I/O.
     */
    @NotNull CompletableFuture<Void> apply();

    /**
     * Set a key/value entry in the store.
     * @param key the non-null key.
     * @param value a value. If null, remove the entry.
     * @return this
     */
    @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value);

    /**
     * Remove a value.
     * @param key the key to remove.
     * @return this.
     */
    default @NotNull MetaDataTransaction remove(@NotNull String key) {
        setKeyValue(key, null);
        return this;
    }

}
