package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.*;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.TraitElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class TraitArgument extends CustomArgument<TraitElement, String> {
    public TraitArgument(String nodeName) {
        super(new TextArgument(nodeName), TraitArgument::parse);

        this.replaceSuggestions(ArgumentSuggestions.strings(info ->
                    SFMain.traitsElementRegistry.getKeys().toArray(String[]::new)
                ));
    }

    public static TraitElement parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        TraitElement elem = SFMain.traitsElementRegistry.get(info.input());
        if (elem == null) {
            throw CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("registry.trait.unknown"),
                    PluginMessageType.ERROR
            ));
        }

        return elem;
    }
}
