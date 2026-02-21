package io.github.meyllane.sfmain.utils;

public enum PluginMessageType {
    SUCCESS("#6FD66F"),
    ERROR("#D66F6F"),
    INFO("#6F96D6");
    private final String color;

    PluginMessageType(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
