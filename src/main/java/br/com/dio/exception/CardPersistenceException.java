package br.com.dio.exception;

public class CardPersistenceException extends RuntimeException{
    public CardPersistenceException(String message) {
        super(message);
    }

    public CardPersistenceException() {
    }

    public CardPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
