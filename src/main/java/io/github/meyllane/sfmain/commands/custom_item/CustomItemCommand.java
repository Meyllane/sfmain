package io.github.meyllane.sfmain.commands.custom_item;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.CustomItemBase;
import io.github.meyllane.sfmain.domain.elements.Quality;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class CustomItemCommand {
    public static void register() {
        new CommandTree("custom_item").thenNested(
                new LiteralArgument("give"),
                new EntitySelectorArgument.OnePlayer("player"),
                new TextArgument("custom_item").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                   return SFMain.customItemsRegistry.getShortNames().toArray(String[]::new);
                })),
                new StringArgument("quality").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                    return Arrays.stream(Quality.values()).map(Quality::getName).toArray(String[]::new);
                })),
                new IntegerArgument("amount").setOptional(true)
                        .executesPlayer(CustomItemCommand::execute)
        ).register();
    }

    private static void execute(Player player, CommandArguments args) {
        try {
            CustomItemCommand.handleExecute(player, args);
        } catch (Exception e) {
            CustomItemCommand.handleErrors(player, e);
        }
    }

    private static void handleExecute(Player player, CommandArguments args) {
        String customItemName = args.getByClassOrDefault("custom_item", String.class, "");
        String qualityName = args.getByClassOrDefault("quality", String.class, Quality.NORMAL.getName());
        Quality quality = Quality.getByName(qualityName);
        Player target = args.getByClass("player", Player.class);
        int amount = args.getByClassOrDefault("amount", Integer.class, 1);

        CustomItemBase item = SFMain.customItemsRegistry.getByShort(customItemName);

        if (item == null) throw new SFException(ErrorMessage.get("registry.custom_item.unknown"));

        ItemStack stack = item.toItemStack(quality, amount);

        target.give(Collections.singleton(stack), false);
    }

    private static void handleErrors(Player player, Throwable ex) {
        ex.printStackTrace();
    }

    private static void handleCompletion(Player player, CustomItemBase item) {

    }
}
