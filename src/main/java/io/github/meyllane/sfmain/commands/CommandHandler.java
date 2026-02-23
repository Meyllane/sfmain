package io.github.meyllane.sfmain.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.CompletionException;

/**
 * Abstract base class for handling commands that operate on a specific target object.
 *
 * <p>Provides a structured pipeline for parsing command arguments, executing commands,
 * and applying updates to a target object. Implementations define the logic for a
 * specific command context, including how to parse arguments, update the target,
 * persist changes, and report completion.
 *
 * <p>The execution pipeline runs asynchronously via Paper's scheduler to avoid
 * blocking the main thread, then returns to the main thread for completion handling.
 *
 * @param <T> the type of the target object being updated
 * @param <V> the type of the update value produced by {@link #parse(CommandArguments)} and applied to {@code T}
 */
public abstract class CommandHandler<T, V> {

    public static final String OPERATION_NODE = "operationValue";
    public static final String TARGET_NODE = "targetValue";

    /**
     * Builds the command branch structure, registering nodes and arguments
     * for this handler in the command tree.
     *
     * @return the branch to append;
     */
    public abstract Argument<String> buildBranch();

    /**
     * Persists the updated target object, typically by calling the relevant service.
     *
     * @param target the target object of type {@code T} to persist
     */
    public abstract void persist(T target);

    /**
     * Parses the provided command arguments into an update value of type {@code V}.
     *
     * @param args the command arguments to parse
     * @return the parsed update value
     */
    public abstract V parse(CommandArguments args);

    /**
     * Executes the command for the given player using the provided arguments.
     * Implementations should delegate to {@link #handleExecution(Player, CommandArguments, Plugin)}.
     *
     * @param player the player executing the command
     * @param args   the command arguments
     */
    public void execute(Player player, CommandArguments args) {
        this.handleExecution(player, args, SFMain.getPlugin(SFMain.class));
    }

    /**
     * Applies an update value to the target, according to the specified operation.
     *
     * @param target    the target object of type {@code T} to update
     * @param value     the update value of type {@code V} to apply to {@code target}
     * @param operation the operation defining how the update should be applied
     */
    public abstract void update(T target, V value, CommandOperation operation);

    /**
     * Resolves the {@link CommandOperation} from the provided command arguments
     * by looking up the operation name node.
     *
     * @param args the command arguments containing the operation name
     * @return the matching {@link CommandOperation}
     * @throws RuntimeException if no matching {@link CommandOperation} is found
     */
    public CommandOperation getOperation(CommandArguments args) {
        String operationName = args.getByClassOrDefault(OPERATION_NODE, String.class, "");

        return CommandOperation.getByName(operationName);
    }

    /**
     * Runs the full command execution pipeline asynchronously.
     *
     * <p>The async portion parses arguments, retrieves the target, applies the update,
     * and persists the result. Once complete, execution returns to the main thread
     * to handle errors or invoke {@link #handleCompletion(CommandResult, Player)}.
     *
     * @param player the player executing the command
     * @param args   the command arguments
     * @param plugin the plugin instance used to schedule tasks
     */
    public void handleExecution(Player player, CommandArguments args, Plugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Throwable ex = null;
            CommandResult<T, V> result = null;

            try {
                V updateValue = this.parse(args);
                T target = Objects.requireNonNull(args.getByClass(TARGET_NODE, this.getTargetClass()));
                CommandOperation operation = this.getOperation(args);

                this.update(target, updateValue, operation);
                this.persist(target);
                result = new CommandResult<>(target, updateValue, operation);
            } catch (Exception e) {
                ex = e;
            }

            final Throwable finalEx = ex;
            final CommandResult<T, V> finalResult = result;

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (finalEx != null) {
                    this.handleErrors(finalEx, player);
                    return;
                };

                this.handleCompletion(finalResult, player);
            });
        });
    }

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

    /**
     * Returns the runtime class of the target type {@code T}.
     *
     * <p>This is required to work around type erasure when retrieving the target
     * from command arguments at runtime.
     *
     * @return the {@link Class} object representing {@code T}
     */
    protected abstract Class<T> getTargetClass();

    /**
     * Handles success feedback at the end of the execution pipeline.
     * Must be called on Paper's main thread.
     *
     * @param result the {@link CommandResult} containing the target, update value, and operation
     * @param player the {@link Player} to which the completion message should be sent
     */
    public abstract void handleCompletion(CommandResult<T, V> result, Player player);
}