package com.churchsoft.members.repo.projection;

import com.churchsoft.members.constant.Gender;
import com.churchsoft.members.constant.MinistryAffiliation;

public interface GenderBreakdownProjection {
    MinistryAffiliation getAffiliation();
    Gender getGender();
    Long getCount();
}