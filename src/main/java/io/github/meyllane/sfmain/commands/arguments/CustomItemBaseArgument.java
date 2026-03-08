package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.*;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.CustomItemBase;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;

public class CustomItemBaseArgument extends CustomArgument<CustomItemBase, String> {
    public CustomItemBaseArgument(String nodeName) {
        super(new TextArgument(nodeName), CustomItemBaseArgument::parse);

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return SFMain.customItemsRegistry.getShortNames().toArray(String[]::new);
        }));
    }

    public static CustomItemBase parse(CustomArgumentInfo<String> info) {
        CustomItemBase item = SFMain.customItemsRegistry.getByShort(info.input());

        if (item == null) {
            throw new SFException(ErrorMessage.get("registry.custom_item.unknown"));
        }

        return item;
    }
}
