package com.example.leavemanagementsystem.enums;

public enum LeaveType {
    SICK("SICK"),
    EXAM("EXAM"),
    ANNUAL("ANNUAL"),
    COMPASSIONATE("COMPASSIONATE");

    private final String name;

    LeaveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
