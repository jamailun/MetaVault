package fr.jamailun.metaVault.storage;

import fr.jamailun.metaVault.storage.exception.StorageInitException;
import fr.jamailun.metaVault.storage.sqlite.SqliteProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public final class StorageProviderFactory {

    private final ConfigurationSection config;
    private Storage storage;

    public StorageProviderFactory(@NotNull ConfigurationSection config) {
        this.config = config;
    }

    /**
     * Get the storage. Should probably be done async.
     * @return a storage instance. Will compute it on the first call.
     * @throws StorageInitException if an issue occurred during storage initialization.
     */
    public @NotNull Storage getStorage() throws StorageInitException {
        if(storage != null) return storage;

        String type = config.getString("type");
        if(type == null)
            throw new StorageInitException("Could not get the 'type' configuration property.");
        ConfigurationSection section = config.getConfigurationSection(type.toLowerCase());
        if(section == null)
            throw new StorageInitException("Could not storage '" + type + "' section.");

        StorageProvider<?> provider = switch (type.toLowerCase()) {
            case "sqlite" -> new SqliteProvider(section);
            default -> throw new StorageInitException("Unknown storage type: '" + type + "'.");
        };

        // Open the storage
        storage = provider.get();

        return storage;
    }

}
