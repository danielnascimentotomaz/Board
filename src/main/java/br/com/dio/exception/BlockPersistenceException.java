package br.com.dio.exception;

import lombok.extern.slf4j.Slf4j;


public class BlockPersistenceException extends RuntimeException{
    public BlockPersistenceException() {
    }

    public BlockPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockPersistenceException(String message) {
        super(message);
    }
}
