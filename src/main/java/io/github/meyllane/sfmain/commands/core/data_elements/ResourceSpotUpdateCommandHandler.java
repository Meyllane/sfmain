package io.github.meyllane.sfmain.commands.core.data_elements;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;

import java.io.IOException;

public abstract class ResourceSpotUpdateCommandHandler<U> extends DataElementUpdateCommandHandler<ResourceSpot, U> {
    @Override
    protected void saveConfig() throws IOException {
        SFMain.resourceSpotsRegistry.saveConfig();
    }

    @Override
    protected void handleCompletion(Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Le point de collecte a bien été mis à jour !",
                PluginMessageType.SUCCESS
        ));
    }
}
