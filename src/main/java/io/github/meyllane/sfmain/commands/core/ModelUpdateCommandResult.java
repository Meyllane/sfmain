package io.github.meyllane.sfmain.commands.core;

public record ModelUpdateCommandResult<M, U>(M target, U updateValue, CommandOperation operation) {
}
