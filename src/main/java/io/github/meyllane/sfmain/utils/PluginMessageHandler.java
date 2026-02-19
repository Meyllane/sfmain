package io.github.meyllane.sfmain.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class PluginMessageHandler {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    public static Component getPluginHeaderComponent() {
        return mm.deserialize("<color:white>[</color><gradient:#AAE2FF:#F88CFD>SFMain</gradient><color:white>]</color> ");
    }

    public static Component buildPluginMessageComponent(String message, PluginMessageType type) {
        Component finalMessage = Component.text(message).color(TextColor.fromHexString(type.getColor()));

        return PluginMessageHandler.getPluginHeaderComponent()
                .append(finalMessage);
    }
}
