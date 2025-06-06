package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.Storage;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Storage into a YAML file.
 */
public class YamlStorage implements Storage {

    private final File file;
    private final YamlConfiguration config;

    private final Map<UUID, YamlMetaDataStore> openedStores = new HashMap<>();

    YamlStorage(@NotNull File file) {
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public @NotNull String getType() {
        return "yaml";
    }

    @Override
    public @NotNull YamlMetaDataStore getStore(@NotNull UUID owner) {
        return openedStores.computeIfAbsent(owner, k -> new YamlMetaDataStore(config, k, this::save));
    }

    private synchronized void save() {
        try {
            config.save(file);
        } catch(IOException e) {
            throw new TransactionFailedException("Could not save YAML file.", e);
        }
    }
}
