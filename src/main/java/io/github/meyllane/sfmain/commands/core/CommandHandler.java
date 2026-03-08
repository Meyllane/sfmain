package io.github.meyllane.sfmain.commands.core;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandHandler {
    /**
     * Builds the command branch structure, registering nodes and arguments
     * for this handler in the command tree.
     *
     * @return the branch to append;
     */
    public abstract Argument<String> buildBranch();

    /**
     * Executes the command for the given player using the provided arguments.
     * Implementations should delegate to {@link #handleExecution(Player, CommandArguments, JavaPlugin)}.
     *
     * @param player the player executing the command
     * @param args   the command arguments
     */
    public void execute(Player player, CommandArguments args) {
        this.handleExecution(player, args, SFMain.getPlugin(SFMain.class));
    }

    public abstract void handleExecution(Player player, CommandArguments args, JavaPlugin plugin);

    /**
     * Handles errors that occurred during command execution.
     *
     * <p>If the exception is a known {@link SFException}, its message is sent directly
     * to the player. Otherwise, the stack trace is printed and a generic error message
     * is sent. Must be called on the main thread.
     *
     * @param ex     the exception that was thrown during execution
     * @param sender the player to notify of the error
     */
    protected void handleErrors(Throwable ex, Player sender) {
        String message;
        if (ex instanceof SFException sfex) {
            message = sfex.getMessage();
        } else {
            ex.printStackTrace();
            message = ErrorMessage.get("general.unexpected_error");
        }

        sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                message,
                PluginMessageType.ERROR
        ));
    }
}
