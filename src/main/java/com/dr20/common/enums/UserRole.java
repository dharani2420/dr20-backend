package com.dr20.common.enums;

public enum UserRole {
    PATIENT, DOCTOR, NURSE, PHYSIOTHERAPIST, LAB_TECH, ELDER_CARE, ADMIN;

    public boolean isStaff() {
        return this != PATIENT;
    }
}
