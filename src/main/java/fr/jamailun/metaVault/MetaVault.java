package fr.jamailun.metaVault;

import fr.jamailun.metaVault.commands.MetaVaultCommand;
import fr.jamailun.metaVault.storage.Stoppable;
import fr.jamailun.metaVault.storage.Storage;
import fr.jamailun.metaVault.storage.StorageProviderFactory;
import fr.jamailun.metaVault.storage.exception.StorageInitException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Level;

/**
 * Plugin entry point.
 */
public final class MetaVault extends JavaPlugin {

    private static MetaVault instance;
    private Storage storage;
    @Getter private static @Nullable String initError;

    @Override
    public void onLoad() {
        instance = this;

        // Read
        saveDefaultConfig();
        ConfigurationSection storageConfig = getConfig().getConfigurationSection("storage");
        if(storageConfig == null) {
            initError = "Missing 'storage' section in the configuration.";
            error("Could not initialize plugin. " + initError);
            return;
        }

        try {
            storage = new StorageProviderFactory(storageConfig).getStorage();
        } catch (StorageInitException e) {
            initError = e.getMessage();
            error("Could not initialize plugin.", e);
            return;
        }

        // Register the storage (only if success)
        Bukkit.getServicesManager().register(Storage.class, storage, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        // Register command
        new MetaVaultCommand(storage, initError);
    }

    @Override
    public void onDisable() {
        if(storage instanceof Stoppable stoppable) {
            stoppable.stop();
            info("Storage stopped.");
        }
    }

    public static void info(@NotNull String message) {
        instance.getLogger().log(Level.INFO, message);
    }

    public static void error(@NotNull String message) {
        instance.getLogger().log(Level.SEVERE, message);
    }

    public static void error(@NotNull String message, @NotNull Throwable throwable) {
        instance.getLogger().log(Level.SEVERE, message, throwable);
    }

    public static @NotNull File dataFolder() {
        return instance.getDataFolder();
    }

}
