package fr.jamailun.metaVault.storage.exception;

public class TransactionFailedException extends RuntimeException {

    public TransactionFailedException(String details, Throwable t) {
        super(details, t);
    }

}
