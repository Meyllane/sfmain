package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class MasteryArgument extends CustomArgument<MasteryElement, String> {
    public MasteryArgument(String nodeName) {
        super(new StringArgument(nodeName), MasteryArgument::parse);

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return SFMain.masteriesElementRegistry.getKeys().toArray(String[]::new);
        }));
    }

    public static MasteryElement parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        MasteryElement elem = SFMain.masteriesElementRegistry.get(info.input());

        if (elem == null) {
            throw CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("registry.mastery.unknown"),
                    PluginMessageType.ERROR
            ));
        }

        return elem;
    }
}
