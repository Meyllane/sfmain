package io.github.meyllane.sfmain.errors;

public class SFException extends RuntimeException {
    public SFException(String message) {
        super(message);
    }

    public SFException(String message, Throwable cause) {
        super(message, cause);
    }
}
