package io.github.meyllane.sfmain.commands.core;

import dev.jorel.commandapi.executors.CommandArguments;

public interface OperableCommandHandler {
    CommandOperation getOperation(CommandArguments args);
}
