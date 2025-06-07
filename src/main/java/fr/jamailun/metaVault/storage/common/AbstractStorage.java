package fr.jamailun.metaVault.storage.common;

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
    }

    @Override
    public void stopObserving(@NotNull Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void stopObserving(@NotNull Plugin plugin) {
        observers.removeIf(o -> Objects.equals(plugin, o.getPlugin()));
    }

    /**
     * Propagate state change.
     * @param owner UUID owing the value.
     * @param key data-key.
     * @param value nullable data-value.
     */
    protected void propagateChange(@NotNull UUID owner, @NotNull String key, @Nullable String value) {
        observers.forEach(o -> o.onValueChanged(owner, key, value));
    }

}
