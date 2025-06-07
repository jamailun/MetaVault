package fr.jamailun.metaVault.storage.common;

import fr.jamailun.metaVault.observer.Observer;
import fr.jamailun.metaVault.storage.Storage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

}
