package fr.jamailun.metaVault.storage.sqlite.transaction;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A full transaction, buildable.
 */
public class SqliteTransaction implements MetaDataTransaction {

    private final Supplier<Connection> sqlSupplier;
    private final String table;

    private final List<SqliteTransactionStep> steps = new ArrayList<>();
    private final Set<Runnable> callbacks = new HashSet<>();

    public SqliteTransaction(@NotNull String table, @NotNull Supplier<Connection> sqlSupplier) {
        this.sqlSupplier = sqlSupplier;
        this.table = table;
    }

    public void addCallback(@NotNull Runnable callback) {
        callbacks.add(callback);
    }

    public void addStep(@NotNull String sql, @NotNull List<String> args) {
        steps.add(new SqliteTransactionStep(sql, args));
    }

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        Connection sql = sqlSupplier.get();
        for(SqliteTransactionStep step : steps) {
            future = future.thenRunAsync(() -> {
                try {
                    step.apply(sql);
                } catch (SQLException e) {
                    throw new TransactionFailedException("Could not apply SQLite transaction step.", e);
                }
            });
        }
        future.thenRunAsync(() -> callbacks.forEach(Runnable::run));
        future.thenRunAsync(() -> {
            try {
                sql.commit();
            } catch (SQLException e) {
                throw new TransactionFailedException("Could not apply commit SQLite transaction.", e);
            }
        });
        return future;
    }

    @Override
    public @NotNull MetaDataTransaction setKeyValue(@NotNull String key, @Nullable String value) {
        if(value == null) {
            addStep("DELETE FROM "+table+" WHERE kv_key = ?;", List.of(key));
        } else {
            addStep("INSERT OR REPLACE INTO "+table+" VALUES (?, ?);", List.of(key, value));
        }
        return this;
    }
}
