package fr.jamailun.metaVault.storage.sqlite.transaction;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Step part of a transaction.
 */
@RequiredArgsConstructor
public class SqliteTransactionStep {

    private final @NotNull String requestSql;
    private final @NotNull List<String> args;

    /**
     * Apply the step to the current commit.
     * @param sql SQL connection.
     * @throws SQLException if an error occurs.
     */
    public void apply(@NotNull Connection sql) throws SQLException {
        // Connection is NOT in auto-commit.
        // Thus, we directly apply modifications here. The transaction will then commit.
        if(args.isEmpty()) {
            sql.createStatement().executeUpdate(requestSql);
        } else {
            try(PreparedStatement statement = sql.prepareStatement(requestSql)) {
                int idx = 1;
                for (String arg : args) {
                    statement.setString(idx++, arg);
                }
                statement.executeUpdate();
            }
        }

    }

}
