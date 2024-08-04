package com.NWPproj.vacuum.Model;

public class DataTypes {
    public enum StatusEnum {
        ON(1),
        OFF(0),
        DISCHARGING(2);

        private final int value;

        StatusEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static StatusEnum fromValue(int value) {
            for (StatusEnum status : StatusEnum.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }
}
