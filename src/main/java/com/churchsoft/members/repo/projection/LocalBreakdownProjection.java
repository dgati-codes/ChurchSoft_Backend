package com.churchsoft.members.repo.projection;

import com.churchsoft.members.constant.MinistryAffiliation;

public interface LocalBreakdownProjection {
    String getRegion();
    String getDistrict();
    String getAssembly();
    MinistryAffiliation getAffiliation();
    Long getCount();
}