package com.churchsoft.users.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum RoleName {
    FINANCE,
    ADMIN,
    PASTOR,
    LEADER,
    MEMBER,
    GUEST,
    REP,
    ELDER;

    @JsonCreator
    public static RoleName fromValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return RoleName.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid role: " + value + ". Valid roles are: " + Arrays.toString(RoleName.values())
            );
        }
    }
}

