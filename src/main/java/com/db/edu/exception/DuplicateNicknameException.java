package com.db.edu.exception;

public class DuplicateNicknameException extends NicknameSettingException {
    public DuplicateNicknameException(String cause) {
        super(cause);
    }
}
