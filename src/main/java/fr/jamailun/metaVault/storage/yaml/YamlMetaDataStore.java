package fr.jamailun.metaVault.storage.yaml;

import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.exception.TransactionFailedException;
import fr.jamailun.metaVault.storage.sqlite.transaction.SqliteTransaction;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class YamlMetaDataStore implements MetaDataStore {

    @Getter private final UUID owner;
    private final ConfigurationSection section;
    private final Runnable saveFunction;

    public YamlMetaDataStore(@NotNull ConfigurationSection parent, @NotNull UUID owner, @NotNull Runnable saveFunction) {
        this.owner = owner;
        this.saveFunction = saveFunction;
        String path = owner.toString();

        if(parent.isConfigurationSection(path)) {
            section = parent.getConfigurationSection(path);
        } else {
            section = parent.createSection(path);
        }
    }

    @Override
    public @NotNull YamlTransaction newTransaction() {
        return new YamlTransaction(section, saveFunction);
    }

}
