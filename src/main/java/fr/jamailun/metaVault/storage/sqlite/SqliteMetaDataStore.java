package fr.jamailun.metaVault.storage.sqlite;

import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.sqlite.transaction.SqliteTransaction;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.UUID;
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
        SqliteTransaction transaction = new SqliteTransaction(sqlSupplier);
        if( ! tableExists.get()) {
            transaction.addStep(getStructure(), tableName);
            transaction.addCallback(() -> tableExists.set(true));
        }
        return transaction;
    }

    /**
     * Table structure.
     * @return static SQL syntax.
     */
    @Contract(pure = true)
    private static @NotNull String getStructure() {
        return """
               CREATE TABLE IF NOT EXIST ?1 (
                 kv_key varchar(255) PRIMARY KEY,
                 kv_val string NOT NULL
               );
               """;
    }

}
