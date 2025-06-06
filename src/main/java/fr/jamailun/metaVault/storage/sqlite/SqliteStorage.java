package fr.jamailun.metaVault.storage.sqlite;

import fr.jamailun.metaVault.storage.Storage;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SqliteStorage implements Storage {

    private final Connection connection;
    private final Map<UUID, SqliteMetaDataStore> openedStores = new HashMap<>();

    SqliteStorage(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull String getType() {
        return "sqlite";
    }

    @Override
    public @NotNull SqliteMetaDataStore getStore(@NotNull UUID owner) {
        return openedStores.computeIfAbsent(owner, k -> new SqliteMetaDataStore(k, () -> connection));
    }

}
