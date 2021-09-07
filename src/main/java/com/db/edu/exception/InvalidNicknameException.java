package com.db.edu.exception;

public class InvalidNicknameException extends NicknameSettingException {
    public InvalidNicknameException(String cause) {
        super(cause);
    }
}
