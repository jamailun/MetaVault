package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.MetaVault;
import fr.jamailun.metaVault.storage.StorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Provider for a YAML storage.
 */
public class YamlProvider extends StorageProvider<YamlStorage> {

    private final ConfigurationSection config;
    private YamlStorage storage;

    public YamlProvider(@NotNull ConfigurationSection config) {
        this.config = config;
    }

    @Override
    public @NotNull YamlStorage get() {
        if(storage == null)
            storage = generate();
        return storage;
    }

    @Override
    public @NotNull String getName() {
        return "yaml";
    }

    private @NotNull YamlStorage generate() {
        // Read config
        String fileName = readString(config, "file");
        File file = new File(MetaVault.dataFolder(), fileName);
        return new YamlStorage(file);
    }

}
