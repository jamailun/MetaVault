package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.common.ObserveEvent;
import lombok.Getter;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class YamlMetaDataStore implements MetaDataStore {

    @Getter private final UUID owner;
    private final ConfigurationSection section;
    private final Runnable saveFunction;
    private final ObserveEvent observeEvent;

    public YamlMetaDataStore(@NotNull ConfigurationSection parent, @NotNull UUID owner, @NotNull Runnable saveFunction, ObserveEvent observeEvent) {
        this.owner = owner;
        this.saveFunction = saveFunction;
        this.observeEvent = observeEvent;
        String path = owner.toString();

        if(parent.isConfigurationSection(path)) {
            section = parent.getConfigurationSection(path);
        } else {
            section = parent.createSection(path);
        }
    }

    @Override
    public @NotNull MetaDataTransaction newTransaction() {
        return new YamlTransaction(section, saveFunction, this::valueChanged);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.get(key) != null);
    }

    @Override
    public @NotNull CompletableFuture<String> getValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.getString(key));
    }

    private void valueChanged(@NotNull String key, @NotNull String value) {
        observeEvent.apply(owner, key, value);
    }

}
