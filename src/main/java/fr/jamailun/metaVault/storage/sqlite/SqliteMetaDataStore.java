package fr.jamailun.metaVault.storage.sqlite;

import fr.jamailun.metaVault.MetaVault;
import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import fr.jamailun.metaVault.storage.sqlite.transaction.SqliteTransaction;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class SqliteMetaDataStore implements MetaDataStore {

    @Getter private final UUID owner;
    private final String tableName;
    private final Supplier<Connection> sqlSupplier;
    private final AtomicBoolean tableExists = new AtomicBoolean(false);

    public SqliteMetaDataStore(@NotNull UUID owner, @NotNull Supplier<Connection> sqlSupplier) {
        this.owner = owner;
        tableName = "kv_" + owner.toString().replace("-", "");
        this.sqlSupplier = sqlSupplier;
    }

    @Override
    public @NotNull SqliteTransaction newTransaction() {
        SqliteTransaction transaction = new SqliteTransaction(tableName, sqlSupplier);
        if( ! tableExists.get()) {
            transaction.addStep(getStructure(tableName), List.of());
            transaction.addCallback(() -> tableExists.set(true));
        }
        return transaction;
    }

    @Override
    public @NotNull CompletableFuture<Boolean> hasValue(@NotNull String key) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        return future.thenApplyAsync(x -> {
            // Table exists ?
            if(tableDoesNotExist())
                return false;

            // Query value in table
            return queryValue(key) != null;
        });
    }

    @Override
    public @NotNull CompletableFuture<String> getValue(@NotNull String key) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        return future.thenApplyAsync(x -> {
            // Table exists ?
            if(tableDoesNotExist())
                return null;

            // Query value in table
            return queryValue(key);
        });
    }

    /**
     * Table structure.
     * @return static SQL syntax.
     */
    @Contract(pure = true)
    private static @NotNull String getStructure(String table) {
        return "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "kv_key varchar(255) PRIMARY KEY,"
                + "kv_val string NOT NULL"
                + ");";
    }

    private boolean tableDoesNotExist() {
        if(tableExists.get())
            return false;
        try(Statement st = sqlSupplier.get().createStatement()) {
            ResultSet result = st.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "';");
            if(!result.next()) {
                MetaVault.info("table not here !");
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new TransactionFailedException("Could not test for table existence", e);
        }
    }

    private String queryValue(String key) {
        try(PreparedStatement st = sqlSupplier.get().prepareStatement("SELECT kv_val FROM " + tableName + " WHERE kv_key = ?1;")) {
            st.setString(1, key);
            ResultSet result = st.executeQuery();
            if(result.next()) {
                return result.getString("kv_val");
            }
            return null;
        } catch(SQLException e) {
            throw new TransactionFailedException("Could not query SQL", e);
        }
    }

}
