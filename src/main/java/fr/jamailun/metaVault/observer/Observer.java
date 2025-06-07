package fr.jamailun.metaVault.observer;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * An Observer can observe changes in an {@link Observable} object.
 */
public interface Observer {

    /**
     * The plugin loading this observer.
     * @return a non-null plugin instance.
     */
    @NotNull Plugin getPlugin();

    /**
     * Called when the value changed.
     * @param uuid the UUID owning the data.
     * @param key the key of the data.
     * @param value the new value. May be null.
     */
    void onValueChanged(@NotNull UUID uuid, @NotNull String key, @Nullable String value);

}
