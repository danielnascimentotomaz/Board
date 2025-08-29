package br.com.dio.exception;

public class CardActionException extends RuntimeException {
    public CardActionException() {
    }

    public CardActionException(String message) {
        super(message);
    }

    public CardActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
