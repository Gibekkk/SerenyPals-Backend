package com.serenypals.restfulapi.enums;

public enum Role{
    USER("User"),
    PSIKIATER("Psikiater");
    
    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String toString() {
        return role;
    }

    public static boolean isAvailable(String role) {
        for (Role s : Role.values()) {
            if (s.role.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public static Role fromString(String role) {
        for (Role s : Role.values()) {
            if (s.role.equalsIgnoreCase(role)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Role Tidak Diketahui: " + role);
    }
}