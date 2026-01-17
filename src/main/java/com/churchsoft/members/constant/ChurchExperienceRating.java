package com.churchsoft.members.constant;

public enum ChurchExperienceRating {
    VERY_POOR(1), POOR(2), FAIR(3), GOOD(4), EXCELLENT(5);

    private final int stars;

    ChurchExperienceRating(int stars) {
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }
}