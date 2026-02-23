package io.github.meyllane.sfmain.commands;

public record CommandResult<T, V>(T target, V updateValue, CommandOperation operation) {
}
