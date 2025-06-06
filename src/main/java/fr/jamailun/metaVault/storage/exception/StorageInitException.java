package fr.jamailun.metaVault.storage.exception;

/**
 * Storage could not be initialized.
 */
public class StorageInitException extends Exception {

    public StorageInitException(String storageType, String details, Throwable throwable) {
        super("with type " + storageType + ": " + details, throwable);
    }

}
