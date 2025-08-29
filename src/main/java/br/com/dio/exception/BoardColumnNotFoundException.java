package br.com.dio.exception;

public class BoardColumnNotFoundException  extends RuntimeException{
    public BoardColumnNotFoundException() {
    }

    public BoardColumnNotFoundException(String message) {
        super(message);
    }

    public BoardColumnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
