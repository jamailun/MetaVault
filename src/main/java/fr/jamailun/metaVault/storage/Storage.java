package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A storage. Contains any number of {@link MetaDataStore}.
 */
public interface Storage {

    /**
     * Get the Storage type.
     * @return non-empty string.
     */
    @NotNull String getType();

    /**
     * Get the store of something.
     * @param owner the owner.
     * @return a non-null meta-data store.
     */
    @NotNull MetaDataStore getStore(@NotNull UUID owner);

}
