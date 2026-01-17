package com.churchsoft.users.constant;

public enum RoleName {
	FINANCE,
    ADMIN,              // Full system access
    PASTOR,             // Can manage services, sermons, and members
    LEADER,             // Ministry or group leader
    MEMBER,             // Regular church member
    GUEST,               // Limited access, e.g., event registration
    REP,                // Local Assemblies Admins
	ELDER
}
