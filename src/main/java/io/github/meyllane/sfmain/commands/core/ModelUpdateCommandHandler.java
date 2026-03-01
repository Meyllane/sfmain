package io.github.meyllane.sfmain.commands.core;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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
 * @param <M> the type of the Model object being updated
 * @param <U> the type of the update value produced by {@link #parse(CommandArguments)} and applied to {@code T}
 */
public abstract class ModelUpdateCommandHandler<M, U> extends CommandHandler<M> implements OperableCommandHandler{
    public static final String TARGET_NODE = "targetValue";

    /**
     * Persists the updated target object, typically by calling the relevant service.
     *
     * @param target the target object of type {@code T} to persist
     */
    public abstract M persist(M target);

    /**
     * Parses the provided command arguments into an update value of type {@code V}.
     *
     * @param args the command arguments to parse
     * @return the parsed update value
     */
    public abstract U parse(CommandArguments args);

    /**
     * Applies an update value to the target, according to the specified operation.
     *
     * @param target    the target object of type {@code T} to update
     * @param updateValue     the update value of type {@code V} to apply to {@code target}
     * @param operation the operation defining how the update should be applied
     */
    public abstract void update(M target, U updateValue, CommandOperation operation);

    /**
     * Resolves the {@link CommandOperation} from the provided command arguments
     * by looking up the operation name node.
     *
     * @param args the command arguments containing the operation name
     * @return the matching {@link CommandOperation}
     * @throws RuntimeException if no matching {@link CommandOperation} is found
     */
    public abstract CommandOperation getOperation(CommandArguments args);

    /**
     * Runs the full command execution pipeline asynchronously.
     *
     * <p>The async portion parses arguments, retrieves the target, applies the update,
     * and persists the result. Once complete, execution returns to the main thread
     * to handle errors or invoke {@link #handleCompletion(ModelUpdateCommandResult, Player)}.
     *
     * @param player the player executing the command
     * @param args   the command arguments
     * @param plugin the plugin instance used to schedule tasks
     */
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        ModelUpdateCommandResult<M, U> result;

        try {
            U updateValue = this.parse(args);
            M target = Objects.requireNonNull(args.getByClass(TARGET_NODE, this.getTargetClass()));
            CommandOperation operation = this.getOperation(args);

            this.update(target, updateValue, operation);
            result = new ModelUpdateCommandResult<>(target, updateValue, operation);
        } catch (Exception e) {
            this.handleErrors(e, player);
            return;
        }

        final ModelUpdateCommandResult<M, U> finalResult = result;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                M updated = this.persist(finalResult.target());
                ModelUpdateCommandResult<M, U> finalRes = new ModelUpdateCommandResult<>(updated, finalResult.updateValue(), finalResult.operation());
                Bukkit.getScheduler().runTask(plugin, () -> this.handleCompletion(finalRes, player));
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
            }
        });
    }

    /**
     * Returns the runtime class of the target type {@code T}.
     *
     * <p>This is required to work around type erasure when retrieving the target
     * from command arguments at runtime.
     *
     * @return the {@link Class} object representing {@code T}
     */
    protected abstract Class<M> getTargetClass();

    /**
     * Handles success feedback at the end of the execution pipeline.
     * Must be called on Paper's main thread.
     *
     * @param result the {@link ModelUpdateCommandResult} containing the target, update value, and operation
     * @param player the {@link Player} to which the completion message should be sent
     */
    public abstract void handleCompletion(ModelUpdateCommandResult<M, U> result, Player player);
}