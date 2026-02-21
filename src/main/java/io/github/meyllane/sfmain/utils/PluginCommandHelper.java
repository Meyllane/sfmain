package io.github.meyllane.sfmain.utils;

import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletionException;

public class PluginCommandHelper {
    public static void handleErrors(Throwable ex, Player sender) {
        String message;
        Throwable root = ex instanceof CompletionException ? ex.getCause() : ex;
        if (root instanceof SFException sfex) {
            message = sfex.getMessage();
        } else {
            root.printStackTrace();
            message = "Une erreur inattendue s'est produite.";
        }

        sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                message,
                PluginMessageType.ERROR
        ));
    }
}
