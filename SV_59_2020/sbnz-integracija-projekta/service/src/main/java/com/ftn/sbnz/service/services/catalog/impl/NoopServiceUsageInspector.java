package com.ftn.sbnz.service.services.catalog.impl;

import com.ftn.sbnz.service.services.catalog.ServiceUsageInspector;

/**
 * @deprecated kept temporarily to preserve binary compatibility during refactor.
 */
@Deprecated(forRemoval = true, since = "0.0.1")
public final class NoopServiceUsageInspector implements ServiceUsageInspector {

    private NoopServiceUsageInspector() {
        throw new UnsupportedOperationException(
                "Replaced by DroolsServiceUsageInspector");
    }

    @Override
    public boolean isOfferingInUse(Long offeringId) {
        throw new UnsupportedOperationException(
                "Replaced by DroolsServiceUsageInspector");
    }
}
