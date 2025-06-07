package fr.jamailun.metaVault.observer;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * An observable element.
 */
public interface Observable {

    /**
     * Register an observer.
     * @param observer non-null instance.
     */
    void observe(@NotNull Observer observer);

    /**
     * Stop an observer from observing.
     * @param observer the observer instance.
     */
    void stopObserving(@NotNull Observer observer);

    /**
     * Stop all {@link Observer} from a plugin.
     * @param plugin plugin instance.
     */
    void stopObserving(@NotNull Plugin plugin);

}
