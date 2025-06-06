package fr.jamailun.metaVault.storage;

import org.jetbrains.annotations.ApiStatus;

/**
 * Internal usage.
 */
@ApiStatus.Internal
public interface Stoppable {

    /**
     * Close the storage.
     */
    void stop();

}
