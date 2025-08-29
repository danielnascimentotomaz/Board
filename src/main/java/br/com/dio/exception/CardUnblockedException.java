package br.com.dio.exception;

public class CardUnblockedException extends RuntimeException{
    public CardUnblockedException() {
    }

    public CardUnblockedException(String message) {
        super(message);
    }

    public CardUnblockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
