package fr.jamailun.metaVault.storage.exception;

/**
 * Configuration is not valid.
 */
public class StorageConfigException extends RuntimeException {

    /**
     * Missing config property.
     * @param storageType storage type.
     * @param missingProperty missing property name.
     */
    public StorageConfigException(String storageType, String missingProperty) {
        super("Missing configuration property '" + missingProperty + "' for storage-type " + storageType + ".");
    }

}
