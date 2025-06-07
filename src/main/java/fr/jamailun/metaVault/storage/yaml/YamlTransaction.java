package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.common.AbstractTransaction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A full transaction, buildable.
 */
public class YamlTransaction extends AbstractTransaction {

    private final ConfigurationSection section;
    private final Runnable saveFunction;
    private final BiConsumer<String, String> observeEvent;

    private final Map<String, String> changes = new HashMap<>();

    public YamlTransaction(@NotNull ConfigurationSection section, @NotNull Runnable saveFunction, BiConsumer<String, String> observeEvent) {
        this.section = section;
        this.saveFunction = saveFunction;
        this.observeEvent = observeEvent;
    }

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        future = future.thenRunAsync(() -> {
            changes.forEach(section::set);
            saveFunction.run();
            callbacks.forEach(Runnable::run);
        });
        future = future.thenRunAsync(() -> changes.forEach(observeEvent));
        return future;
    }

    @Override
    public @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value) {
        changes.put(key, value);
        return this;
    }
}
