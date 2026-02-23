package io.github.meyllane.sfmain.commands;

public enum CommandOperation {
    ADD("add"),
    REMOVE("remove"),
    SET("set"),
    UPDATE("update");

    private final String name;

    CommandOperation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CommandOperation getByName(String name) throws RuntimeException {
        for (CommandOperation operation : values()) {
            if (operation.getName().equals(name)) return operation;
        }

        throw new RuntimeException("No CommandOperation with the name '" + name + "' found.");
    }
}
