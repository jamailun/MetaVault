package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.MetaVault;
import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.common.AbstractTransaction;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A full transaction, buildable.
 */
@RequiredArgsConstructor
public class YamlTransaction extends AbstractTransaction {

    private final ConfigurationSection section;
    private final long fakeLatency;
    private final Runnable saveFunction;
    private final BiConsumer<String, String> observeEvent;

    private final Map<String, String> changes = new HashMap<>();

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        future = future.thenRunAsync(() -> {
            MetaVault.debug("[Yaml::transaction] Starting execution.");
            changes.forEach(section::set);
            if(fakeLatency > 0) {
                try {
                    Thread.sleep(fakeLatency);
                } catch (InterruptedException e) {
                    throw new TransactionFailedException("Fake-latency (" + fakeLatency + "ms)", e);
                }
            }
            saveFunction.run();
            MetaVault.debug("[Yaml::transaction] File-save done.");
            callbacks.forEach(Runnable::run);
            MetaVault.debug("[Yaml::transaction] Callbacks done.");
        });
        future = future.thenRunAsync(() -> {
            changes.forEach(observeEvent);
            MetaVault.debug("[Yaml::transaction] Observers done.");
        });
        return future;
    }

    @Override
    public @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value) {
        changes.put(key, value);
        return this;
    }
}
