package io.github.meyllane.sfmain.commands.core;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModelCreateCommandHandler<M> extends CommandHandler<M> {
    public abstract M parse(CommandArguments args);
    public abstract M create(M model);
    public abstract void updateRegistry(M model);
    public abstract void handleCompletion(M model, Player player);

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        M model;
        try {
            model = this.parse(args);
        } catch (Exception e) {
            this.handleErrors(e, player);
            return;
        }

        final M tempModel = model;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            M updatedModel;
            try {
                updatedModel = this.create(tempModel);
                this.updateRegistry(updatedModel);
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> handleCompletion(updatedModel, player));
        });
    }
}
