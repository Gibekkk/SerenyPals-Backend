package com.serenypals.restfulapi.enums;

public enum TaskStatus{
    NOT_DONE("Belum Selesai"),
    COMPLETED("Selesai"),
    CLAIMED("Sudah Diklaim");
    
    private final String taskStatus;

    TaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String toString() {
        return taskStatus;
    }

    public static boolean isAvailable(String taskStatus) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.taskStatus.equalsIgnoreCase(taskStatus)) {
                return true;
            }
        }
        return false;
    }

    public static TaskStatus fromString(String taskStatus) {
        for (TaskStatus s : TaskStatus.values()) {
            if (s.taskStatus.equalsIgnoreCase(taskStatus)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status Tidak Diketahui: " + taskStatus);
    }
}