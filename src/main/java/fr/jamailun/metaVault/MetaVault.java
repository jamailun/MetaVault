package fr.jamailun.metaVault;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class MetaVault extends JavaPlugin {

    private static MetaVault instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void error(@NotNull String message) {
        instance.getLogger().log(Level.SEVERE, message);
    }

    public static void error(@NotNull String message, @NotNull Throwable throwable) {
        instance.getLogger().log(Level.SEVERE, message, throwable);
    }

}
