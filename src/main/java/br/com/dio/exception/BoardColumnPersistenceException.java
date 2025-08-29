package br.com.dio.exception;

public class BoardColumnPersistenceException extends RuntimeException{
    public BoardColumnPersistenceException() {
    }

    public BoardColumnPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardColumnPersistenceException(String message) {
        super(message);
    }
}
