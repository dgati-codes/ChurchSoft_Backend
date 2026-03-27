package com.churchsoft.members.repo.projection;

import com.churchsoft.members.constant.MinistryAffiliation;

public interface AffiliationCountProjection {
    MinistryAffiliation getAffiliation();
    Long getCount();
}