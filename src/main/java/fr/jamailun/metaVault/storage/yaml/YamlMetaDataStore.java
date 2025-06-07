package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.common.ObserveEvent;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class YamlMetaDataStore implements MetaDataStore {

    @Getter private final UUID owner;
    private final long fakeLatency;
    private final ConfigurationSection section;
    private final Runnable saveFunction;
    private final ObserveEvent observeEvent;

    public YamlMetaDataStore(@NotNull ConfigurationSection parent, @NotNull UUID owner, long fakeLatency, @NotNull Runnable saveFunction, ObserveEvent observeEvent) {
        this.owner = owner;
        this.fakeLatency = fakeLatency;
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
        return new YamlTransaction(section, fakeLatency, saveFunction, this::valueChanged);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.get(key) != null);
    }

    @Override
    public @NotNull CompletableFuture<String> getValue(@NotNull String key) {
        return CompletableFuture.completedFuture(section.getString(key));
    }

    @Override
    public @NotNull @UnmodifiableView CompletableFuture<List<String>> listKeys() {
        List<String> keys = new ArrayList<>(section.getKeys(false));
        return CompletableFuture.completedFuture(Collections.unmodifiableList(keys));
    }

    @Override
    public @NotNull @UnmodifiableView CompletableFuture<Map<String, String>> getAllEntries() {
        Map<String, String> map = new LinkedHashMap<>();
        for(String key : section.getKeys(false))
            map.put(key, section.getString(key));
        return CompletableFuture.completedFuture(Collections.unmodifiableMap(map));
    }

    private void valueChanged(@NotNull String key, @NotNull String value) {
        observeEvent.apply(owner, key, value);
    }

}
