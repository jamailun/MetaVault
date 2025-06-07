package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.common.AbstractStorage;
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
public class YamlStorage extends AbstractStorage {

    private final File file;
    private final YamlConfiguration config;
    private final long fakeLatency;

    private final Map<UUID, YamlMetaDataStore> openedStores = new HashMap<>();

    YamlStorage(@NotNull File file, long fakeLatency) {
        this.file = file;
        this.fakeLatency = fakeLatency;
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public @NotNull String getType() {
        return "yaml";
    }

    @Override
    public @NotNull YamlMetaDataStore getStore(@NotNull UUID owner) {
        return openedStores.computeIfAbsent(owner, k -> new YamlMetaDataStore(config, k, fakeLatency, this::save, this::propagateChange));
    }

    private synchronized void save() {
        try {
            config.save(file);
        } catch(IOException e) {
            throw new TransactionFailedException("Could not save YAML file.", e);
        }
    }
}
