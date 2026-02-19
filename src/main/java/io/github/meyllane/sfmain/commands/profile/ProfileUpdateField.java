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
}
