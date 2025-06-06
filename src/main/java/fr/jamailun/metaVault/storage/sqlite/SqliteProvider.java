package fr.jamailun.metaVault.storage.sqlite;

import fr.jamailun.metaVault.storage.StorageProvider;
import fr.jamailun.metaVault.storage.exception.StorageInitException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provider for a SQLITE storage.
 */
public class SqliteProvider extends StorageProvider<SqliteStorage> {

    private final ConfigurationSection config;
    private SqliteStorage storage;

    public SqliteProvider(@NotNull ConfigurationSection config) {
        this.config = config;
    }

    @Override
    public @NotNull SqliteStorage get() throws StorageInitException {
        if(storage == null)
            storage = generate();
        return storage;
    }

    @Override
    public @NotNull String getName() {
        return "sqlite";
    }

    private @NotNull SqliteStorage generate() throws StorageInitException {
        // Prepare
        try {
            Class.forName("org.sqlite.JDBC");
        } catch(ClassNotFoundException e) {
            throw initException("Could not get class org.sqlite.JDBC", e);
        }

        // Read config
        String file = readString(config, "file");

        // Open I/O
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch(SQLException e) {
            throw initException("Could not open SQLite file.", e);
        }
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw initException("Could not set SQL connection as non auto-commit.", e);
        }
        return new SqliteStorage(connection);
    }

}
