package br.com.dio.exception;

public class BoardColumnAccessException extends RuntimeException{
    public BoardColumnAccessException(String message) {
        super(message);
    }

    public BoardColumnAccessException() {
    }

    public BoardColumnAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
