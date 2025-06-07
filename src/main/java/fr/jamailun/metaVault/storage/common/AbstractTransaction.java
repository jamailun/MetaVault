package fr.jamailun.metaVault.storage.common;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract {@link MetaDataTransaction}, to handle callbacks.
 */
public abstract class AbstractTransaction implements MetaDataTransaction {

    protected final Set<Runnable> callbacks = new HashSet<>();

    @Override
    public @NotNull AbstractTransaction addCallback(@NotNull Runnable callback) {
        callbacks.add(callback);
        return this;
    }

}
