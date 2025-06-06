package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataStore;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class YamlMetaDataStore implements MetaDataStore {

    @Getter private final UUID owner;
    private final ConfigurationSection section;
    private final Runnable saveFunction;

    public YamlMetaDataStore(@NotNull ConfigurationSection parent, @NotNull UUID owner, @NotNull Runnable saveFunction) {
        this.owner = owner;
        this.saveFunction = saveFunction;
        String path = owner.toString();

        if(parent.isConfigurationSection(path)) {
            section = parent.getConfigurationSection(path);
        } else {
            section = parent.createSection(path);
        }
    }

    @Override
    public @NotNull YamlTransaction newTransaction() {
        return new YamlTransaction(section, saveFunction);
    }

    @Override
    public @NotNull Future<Boolean> hasValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.get(key) != null);
    }

    @Override
    public @NotNull Future<String> getValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.getString(key));
    }

}
