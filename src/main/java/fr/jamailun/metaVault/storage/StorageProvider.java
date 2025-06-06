package fr.jamailun.metaVault.storage;

import fr.jamailun.metaVault.storage.exception.StorageConfigException;
import fr.jamailun.metaVault.storage.exception.StorageInitException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for the storage.
 * @param <T> storage provider.
 */
public abstract class StorageProvider<T extends Storage> {

    /**
     * Get the storage.
     * @return a new storage instance.
     */
    public abstract @NotNull T get() throws StorageInitException;

    /**
     * Get storage type.
     * @return the storage name.
     */
    public abstract @NotNull String getName();

    protected @NotNull String readString(@NotNull ConfigurationSection section, @NotNull String key) {
        String file = section.getString(key);
        if(file == null)
            throw new StorageConfigException(getName(), key);
        return file;
    }

    protected StorageInitException initException(String details, Throwable t) {
        return new StorageInitException(getName(), details, t);
    }

}
