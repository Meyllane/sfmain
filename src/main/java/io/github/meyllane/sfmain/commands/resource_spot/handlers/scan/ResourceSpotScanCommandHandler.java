package io.github.meyllane.sfmain.commands.resource_spot.handlers.scan;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.CommandHandler;

import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileRSInteraction;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResourceSpotScanCommandHandler extends CommandHandler {
    private final String SHOW_ALL_VALUE_OPERATION_VALUE = "show_all";
    private final String SHOW_AVAILABLE_OPERATION_VALUE = "show_available";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("scan")
                .then(
                        new MultiLiteralArgument("type", SHOW_ALL_VALUE_OPERATION_VALUE, SHOW_AVAILABLE_OPERATION_VALUE)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            String type = args.getByClassOrDefault("type", String.class, SHOW_ALL_VALUE_OPERATION_VALUE);

            Profile profile = SFMain.userRegistry.get(player.getUniqueId().toString()).getActiveProfile();

            if (profile == null) {
                throw new SFException(ErrorMessage.get("resource_spot.no_profile"));
            }

            Set<ResourceSpot> spots = SFMain.resourceSpotsRegistry.getSpotsInRange(player.getLocation(), 20);

            if (type.equals(SHOW_AVAILABLE_OPERATION_VALUE)) {
                Map<Integer, ProfileRSInteraction> prsMap = profile.getProfileRSInteractions().stream()
                        .collect(Collectors.toMap(ProfileRSInteraction::getResourceSpotID, Function.identity()));

                spots.removeIf(spot -> prsMap.containsKey(spot.getId())
                        && prsMap.get(spot.getId()).getNbInteraction() == spot.getMaxInteraction());
            }

            SFMain.resourceSpotHighlightManager.spawnSpotHighlights(player, profile, spots);

            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    spots.size() + " points de collecte ont été mis en surbrillance.", PluginMessageType.SUCCESS
            ));

        } catch (SFException e) {
            this.handleErrors(e, player);
        }
    }
}
