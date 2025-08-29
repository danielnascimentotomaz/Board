package br.com.dio.exception;

public class CardAlreadyCancelledException extends RuntimeException{
    public CardAlreadyCancelledException() {
    }

    public CardAlreadyCancelledException(String message) {
        super(message);
    }

    public CardAlreadyCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}
