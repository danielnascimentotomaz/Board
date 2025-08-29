package br.com.dio.exception;

public class CardCancelledException extends RuntimeException{
    public CardCancelledException() {
    }

    public CardCancelledException(String message) {
        super(message);
    }

    public CardCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}
