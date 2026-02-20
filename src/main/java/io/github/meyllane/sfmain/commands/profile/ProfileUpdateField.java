package io.github.meyllane.sfmain.commands.profile;

import java.util.Arrays;
import java.util.List;

public enum ProfileUpdateField {
    NAME("name"),
    AGE("age"),
    DESCRIPTION("description"),
    SPECIES("species");

    private final String shortName;

    ProfileUpdateField(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public static List<String> getShortNames() {
        return Arrays.stream(ProfileUpdateField.values())
                .map(ProfileUpdateField::getShortName)
                .toList();
    }

    public static ProfileUpdateField fromShortName(String shortName) {
        for (ProfileUpdateField field : values()) {
            if (field.shortName.equals(shortName)) return field;
        }

        throw new IllegalArgumentException("No " + ProfileUpdateField.class.getName() + " with the shortName " + shortName);
    }
}
