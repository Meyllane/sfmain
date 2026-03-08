package io.github.meyllane.sfmain.domain.elements;

import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;

public enum Quality {
    BAD(1, "Mauvais", -1, "#965454", 40),
    NORMAL(2, "Normal", 0, "#65D152", 60),
    GOOD(3, "Bon", 1, "#52CFD1", 80),
    EXCELLENT(4, "Excellent", 2, "#C252D1", 100);

    private int ID;
    private String name;
    private int value;
    private String color;
    private int baseDurability;

    Quality(int ID, String name, int value, String color, int baseDurability) {
        this.ID = ID;
        this.name = name;
        this.value = value;
        this.color = color;
        this.baseDurability = baseDurability;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public static Quality getByName(String name) {
        for (Quality q : values()) {
            if (q.name.equals(name)) return q;
        }

        throw new SFException(ErrorMessage.get("quality.unknown"));
    }

    public static Quality getByID(Integer ID) {
        for (Quality q : values()) {
            if (q.ID == ID) return q;
        }

        throw new SFException(ErrorMessage.get("quality.unknown"));
    }
}
