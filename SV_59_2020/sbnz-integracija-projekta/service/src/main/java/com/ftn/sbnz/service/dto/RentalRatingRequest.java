package com.ftn.sbnz.service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class RentalRatingRequest {

    @Min(1)
    @Max(5)
    private int score;

    public RentalRatingRequest() {
    }

    public RentalRatingRequest(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
