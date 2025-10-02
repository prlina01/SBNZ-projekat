package com.ftn.sbnz.service.dto;

/**
 * @deprecated Retained for backward compatibility with older API clients.
 *             Use {@link RentRequest} instead.
 */
@Deprecated
public class RentalDTO extends RentRequest {

    public RentalDTO() {
        super();
    }

    public RentalDTO(Long serviceOfferingId, String purpose, int durationDays) {
        super(serviceOfferingId, purpose, durationDays);
    }
}
