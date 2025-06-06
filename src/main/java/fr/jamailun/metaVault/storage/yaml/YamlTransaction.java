package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A full transaction, buildable.
 */
public class YamlTransaction implements MetaDataTransaction {

    private final ConfigurationSection section;
    private final Runnable saveFunction;

    private final Map<String, String> changes = new HashMap<>();
    private final Set<Runnable> callbacks = new HashSet<>();

    public YamlTransaction(@NotNull ConfigurationSection section, @NotNull Runnable saveFunction) {
        this.section = section;
        this.saveFunction = saveFunction;
    }

    @Override
    public void addCallback(@NotNull Runnable callback) {
        callbacks.add(callback);
    }

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        future.thenRunAsync(() -> {
            changes.forEach(section::set);
            saveFunction.run();
            callbacks.forEach(Runnable::run);
        });
        return future;
    }

    @Override
    public @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value) {
        changes.put(key, value);
        return this;
    }
}
