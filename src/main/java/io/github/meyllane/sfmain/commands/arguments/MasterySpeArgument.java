package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class MasterySpeArgument extends CustomArgument<MasterySpeElement, String> {
    public MasterySpeArgument(String nodeName) {
        super(new StringArgument(nodeName), MasterySpeArgument::parse);
    }

    public static MasterySpeElement parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        MasterySpeElement elem = SFMain.masterySpeElementRegistry.get(info.input());

        if (elem == null) {
            throw CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("registry.mastery_spe.unknown"),
                    PluginMessageType.ERROR
            ));
        }

        return elem;
    }
}
