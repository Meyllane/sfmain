package io.github.meyllane.sfmain.errors;

import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public final class ErrorMessage {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("errors");

    public static String get(@PropertyKey(resourceBundle = "errors") String errorNode) {
        return BUNDLE.getString(errorNode);
    }
}
