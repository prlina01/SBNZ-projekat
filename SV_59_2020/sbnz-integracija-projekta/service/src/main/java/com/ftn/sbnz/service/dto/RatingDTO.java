package com.ftn.sbnz.service.dto;

/**
 * @deprecated Retained for backward compatibility with older API clients.
 *             Use {@link RentalRatingRequest} alongside rental identifiers provided via URL path.
 */
@Deprecated
public class RatingDTO extends RentalRatingRequest {

    private Long rentalId;

    public RatingDTO() {
        super();
    }

    public RatingDTO(Long rentalId, int score) {
        super(score);
        this.rentalId = rentalId;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
}
