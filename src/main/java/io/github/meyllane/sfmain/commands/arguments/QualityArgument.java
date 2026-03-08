package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.domain.elements.Quality;

import java.util.Arrays;

public class QualityArgument extends CustomArgument<Quality, String> {
    public QualityArgument(String nodeName) {
        super(new StringArgument(nodeName), QualityArgument::parser);

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return Arrays.stream(Quality.values())
                    .map(Quality::getName)
                    .toArray(String[]::new);
        }));
    }

    public static Quality parser(CustomArgumentInfo<String> info) {
        return Quality.getByName(info.input());
    }
}
