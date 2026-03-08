package io.github.meyllane.sfmain.commands.core.data_elements;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public abstract class DataElementUpdateCommandHandler<D, U> extends CommandHandler {
    public static final String DATA_VALUE_NODE = "target";

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            D data = Objects.requireNonNull(args.getByClass(DATA_VALUE_NODE, getTargetClass()));
            U updateValue = this.parse(player, args);
            this.update(data, updateValue);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
               try {
                   this.saveConfig();
               } catch (Exception e) {
                   Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
                   return;
               }

               Bukkit.getScheduler().runTask(plugin, () -> this.handleCompletion(player));
            });
        } catch (Exception e) {
            this.handleErrors(e, player);
        }
    }

    protected abstract void update(D data, U updateValue);

    protected abstract void saveConfig() throws IOException;

    protected abstract U parse(Player player, CommandArguments args);

    protected abstract Class<D> getTargetClass();

    protected abstract void handleCompletion(Player player);
}
