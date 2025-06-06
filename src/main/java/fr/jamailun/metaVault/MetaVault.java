package fr.jamailun.metaVault;

import fr.jamailun.metaVault.storage.Stoppable;
import fr.jamailun.metaVault.storage.Storage;
import fr.jamailun.metaVault.storage.StorageProviderFactory;
import fr.jamailun.metaVault.storage.exception.StorageInitException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class MetaVault extends JavaPlugin {

    private static MetaVault instance;
    private Storage storage;

    @Override
    public void onLoad() {
        instance = this;

        // Read
        saveDefaultConfig();
        ConfigurationSection storageConfig = getConfig().getConfigurationSection("storage");
        if(storageConfig == null) {
            error("Could not initialize plugin. Missing 'storage' section in the configuration !");
            error("Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            storage = new StorageProviderFactory(storageConfig).getStorage();
        } catch (StorageInitException e) {
            error("Could not initialize plugin.", e);
            error("Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        // Register the storage
        Bukkit.getServicesManager().register(Storage.class, storage, this, ServicePriority.Normal);
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

}
