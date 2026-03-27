package com.churchsoft.members.repo.projection;

import com.churchsoft.members.constant.MinistryAffiliation;

public interface DashboardStatsProjection {
    MinistryAffiliation getAffiliation();
    Long getTotal();
    Long getCurrent();
    Long getPrevious();
}