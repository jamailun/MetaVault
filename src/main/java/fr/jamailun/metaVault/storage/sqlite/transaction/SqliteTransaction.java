package fr.jamailun.metaVault.storage.sqlite.transaction;

import fr.jamailun.metaVault.MetaVault;
import fr.jamailun.metaVault.storage.MetaDataTransaction;
import fr.jamailun.metaVault.storage.common.AbstractTransaction;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A full transaction, buildable.
 */
public class SqliteTransaction extends AbstractTransaction {

    private final Supplier<Connection> sqlSupplier;
    private final String table;
    private final BiConsumer<String, String> observeEvent;

    private final Map<String, String> changes = new HashMap<>();
    private final List<SqliteTransactionStep> steps = new ArrayList<>();

    public SqliteTransaction(@NotNull String table, @NotNull Supplier<Connection> sqlSupplier, BiConsumer<String, String> observeEvent) {
        this.sqlSupplier = sqlSupplier;
        this.table = table;
        this.observeEvent = observeEvent;
    }

    public void addStep(@NotNull String sql, @NotNull List<String> args) {
        steps.add(new SqliteTransactionStep(sql, args));
    }

    @Override
    public @NotNull CompletableFuture<Void> apply() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        Connection sql = sqlSupplier.get();
        MetaVault.debug("[Sqlite::transaction] Starting execution.");
        for(SqliteTransactionStep step : steps) {
            future = future.thenRunAsync(() -> {
                try {
                    step.apply(sql);
                } catch (SQLException e) {
                    throw new TransactionFailedException("Could not apply SQLite transaction step.", e);
                }
            });
        }
        future.thenRunAsync(() -> {
            try {
                sql.commit();
            } catch (SQLException e) {
                throw new TransactionFailedException("Could not apply commit SQLite transaction.", e);
            } finally {
                MetaVault.debug("[Sqlite::transaction] Commit done.");
            }
        });
        future.thenRunAsync(() -> {
            callbacks.forEach(Runnable::run);
            MetaVault.debug("[Sqlite::transaction] Callbacks done.");
        });
        future = future.thenRunAsync(() -> {
            changes.forEach(observeEvent);
            MetaVault.debug("[Sqlite::transaction] Observers done.");
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
        changes.put(key, value);
        return this;
    }
}
