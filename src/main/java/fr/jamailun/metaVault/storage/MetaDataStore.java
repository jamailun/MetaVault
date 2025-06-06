package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

}
