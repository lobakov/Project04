package com.db.edu.exception;

public class UnknownCommandException extends CommandProcessException {
    public UnknownCommandException(String cause) {
        super(cause);
    }
}
