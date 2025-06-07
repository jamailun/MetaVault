package fr.jamailun.metaVault.storage.common;

import fr.jamailun.metaVault.MetaVault;
import fr.jamailun.metaVault.observer.Observer;
import fr.jamailun.metaVault.storage.Storage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract {@link Storage}, to handle observability.
 */
public abstract class AbstractStorage implements Storage {

    protected final Set<Observer> observers = new HashSet<>();

    @Override
    public void observe(@NotNull Observer observer) {
        observers.add(observer);
        MetaVault.info("Observer registered: " + observer + ".");
    }

    @Override
    public void stopObserving(@NotNull Observer observer) {
        observers.remove(observer);
        MetaVault.info("Observer un-registered: " + observer + ".");
    }

    @Override
    public void stopObserving(@NotNull Plugin plugin) {
        observers.removeIf(o -> Objects.equals(plugin, o.getPlugin()));
        MetaVault.info("Plugin un-registered: " + plugin.getName() + ".");
    }

    /**
     * Propagate state change.
     * @param owner UUID owing the value.
     * @param key data-key.
     * @param value nullable data-value.
     */
    protected void propagateChange(@NotNull UUID owner, @NotNull String key, @Nullable String value) {
        observers.forEach(o -> {
            try {
                o.onValueChanged(owner, key, value);
            } catch(Exception e) {
                MetaVault.error("Error on observer " + o + "for entry " + owner + "["+key+"] <- "+value+")", e);
            }
        });
    }

}
