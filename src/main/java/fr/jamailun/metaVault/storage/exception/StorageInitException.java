package fr.jamailun.metaVault.storage.exception;

/**
 * Storage could not be initialized.
 */
public class StorageInitException extends Exception {

    public StorageInitException(String storageType, String details, Throwable throwable) {
        super("With type " + storageType + ": " + details, throwable);
    }

    public StorageInitException(String details) {
        super(details);
    }

}
