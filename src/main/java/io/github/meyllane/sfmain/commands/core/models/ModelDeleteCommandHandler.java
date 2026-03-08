package io.github.meyllane.sfmain.commands.core.models;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModelDeleteCommandHandler<M> extends CommandHandler {
    public abstract M parse(CommandArguments args);
    public abstract void delete(M model);
    public abstract void updateRegistry(M model);
    public abstract void handleCompletion(M model, Player player);

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        M elem;
        try {
            elem = this.parse(args);
            this.updateRegistry(elem);
        } catch (Exception e) {
            this.handleErrors(e, player);
            return;
        }

        final M finalElem = elem;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                this.delete(finalElem);
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> this.handleCompletion(finalElem, player));
        });
    }
}
