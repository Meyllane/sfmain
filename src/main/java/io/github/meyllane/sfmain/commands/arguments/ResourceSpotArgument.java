package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;

public class ResourceSpotArgument extends CustomArgument<ResourceSpot, String> {
    public ResourceSpotArgument(String nodeName) {
        super(
                new TextArgument(nodeName),
                ResourceSpotArgument::parse
        );

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return SFMain.resourceSpotsRegistry.getKeys().toArray(String[]::new);
        }));
    }

    public static ResourceSpot parse(CustomArgumentInfo<String> info) {
        ResourceSpot spot = SFMain.resourceSpotsRegistry.get(info.input());

        if (spot == null) {
            throw new SFException(ErrorMessage.get("resource_spot.unknown_name"));
        }

         return spot;
    }
}
