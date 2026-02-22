package io.github.meyllane.sfmain.commands.profile.update;

public enum ProfileUpdateOperation {
    ADD("add"),
    REMOVE("remove"),
    UPDATE("update"),
    SET("set");
    private final String name;

    ProfileUpdateOperation(String name) {
        this.name = name;
    }

    public static ProfileUpdateOperation getByName(String name) {
        for (ProfileUpdateOperation op : values()) {
            if (op.name.equals(name)) return op;
        }

        throw new RuntimeException("No " + ProfileUpdateOperation.class.getName() + " with the name " + name);
    }

    public String getName() {
        return name;
    }
}
