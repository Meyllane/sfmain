package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.SpeciesElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class SpeciesArgument extends CustomArgument<SpeciesElement, String> {
    public SpeciesArgument(String nodeName) {
        super(
                new StringArgument(nodeName),
                SpeciesArgument::parse
        );

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return SFMain.speciesElementRegistry.getKeys().toArray(String[]::new);
        }));
    }

    private static SpeciesElement parse(CustomArgument.CustomArgumentInfo<String> info) throws CustomArgument.CustomArgumentException {
        SpeciesElement species = SFMain.speciesElementRegistry.get(info.input());

        if (species == null) {
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("registry.species.unknown"),
                    PluginMessageType.ERROR
            ));
        }

        return species;
    }
}
