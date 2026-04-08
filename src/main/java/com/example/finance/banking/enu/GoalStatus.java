package com.example.finance.banking.enu;

public enum GoalStatus {
    ACTIVE("ACTIVE"),
    ACHIEVED("ACHIEVED");

    private final String value;

    GoalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GoalStatus fromValue(String value) {
        for (GoalStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}