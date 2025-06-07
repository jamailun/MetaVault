package fr.jamailun.metaVault.storage.common;

import fr.jamailun.metaVault.storage.MetaDataTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTransaction implements MetaDataTransaction {

    protected final Set<Runnable> callbacks = new HashSet<>();

    @Override
    public void addCallback(@NotNull Runnable callback) {
        callbacks.add(callback);
    }

}
