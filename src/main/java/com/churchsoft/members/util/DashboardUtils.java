package com.churchsoft.members.util;

import com.churchsoft.members.constant.MinistryAffiliation;

public class DashboardUtils {

    public static String mapAffiliationToGroup(MinistryAffiliation aff) {
        return switch (aff) {
            case CHILDREN -> "Children";
            case JUNIOR_YOUTH -> "Junior Youth";
            case SENIOR_YOUTH -> "Senior Youth";
            case MEN, WOMEN -> "Adults";
            default -> "Others";
        };
    }

    public static long safe(Long val) {
        return val == null ? 0 : val;
    }
}