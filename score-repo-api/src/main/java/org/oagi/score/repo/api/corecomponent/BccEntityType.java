package org.oagi.score.repo.api.corecomponent;

public enum BccEntityType {

    Attribute(0),
    Element(1);

    private final int value;

    BccEntityType(int value) {
        this.value = value;
    }

    public static BccEntityType valueOf(int value) {
        for (BccEntityType state : BccEntityType.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public int getValue() {
        return value;
    }

}
