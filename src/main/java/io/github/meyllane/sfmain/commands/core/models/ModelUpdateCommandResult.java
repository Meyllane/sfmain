package io.github.meyllane.sfmain.commands.core.models;

public record ModelUpdateCommandResult<M, U>(M target, U updateValue, CommandOperation operation) {
}
