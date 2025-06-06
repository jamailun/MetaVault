package fr.jamailun.metaVault.storage.sqlite.transaction;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SqliteTransaction implements MetaDataTransaction {

    private final List<SqliteTransactionStep> steps = new ArrayList<>();
    private final Supplier<Connection> sqlSupplier;
    private final Set<Runnable> callbacks = new HashSet<>();

    public SqliteTransaction(@NotNull Supplier<Connection> sqlSupplier) {
        this.sqlSupplier = sqlSupplier;
    }

    public void addCallback(@NotNull Runnable callback) {
        callbacks.add(callback);
    }

    public void addStep(@NotNull String sql, @NotNull String @NotNull ... args) {
        int index = 0;
        for(String arg : args) {
            sql = sql.replace("?" + (++index), arg);
        }
        //TODO apply
    }

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for(var step : steps) {
            future = future.thenRunAsync(step::apply);
        }
        future.thenRunAsync(() -> callbacks.forEach(Runnable::run));
        return future;
    }

    @Override
    public @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value) {
        //TODO this
        return this;
    }
}
