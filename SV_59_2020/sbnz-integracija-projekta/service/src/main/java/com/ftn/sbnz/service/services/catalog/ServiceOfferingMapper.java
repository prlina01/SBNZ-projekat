package com.ftn.sbnz.service.services.catalog;

import com.ftn.sbnz.model.catalog.ServiceOffering;
import com.ftn.sbnz.service.catalog.dto.ServiceOfferingResponse;
import org.springframework.stereotype.Component;

@Component
public class ServiceOfferingMapper {

    public ServiceOfferingResponse toResponse(ServiceOffering offering) {
        ServiceOfferingResponse response = new ServiceOfferingResponse();
        response.setId(offering.getId());
        response.setName(offering.getName());
        response.setProvider(offering.getProviderName());
        response.setPurpose(offering.getPurpose());
        response.setCpuPerformance(offering.getCpuPerformance());
        response.setVcpuCount(offering.getVcpuCount());
        response.setGpuModel(offering.getGpuModel());
        response.setGpuVram(offering.getGpuVram());
        response.setRam(offering.getRam());
        response.setStorageCapacity(offering.getStorageCapacity());
        response.setStorageType(offering.getStorageType());
        response.setEncryptedStorage(offering.isEncryptedStorage());
        response.setNetworkBandwidth(offering.getNetworkBandwidth());
        response.setDdosProtection(offering.isDdosProtection());
        response.setHighAvailability(offering.isHighAvailability());
        response.setRegion(offering.getRegion());
        response.setEcoFriendly(offering.isEcoFriendly());
        response.setPricePerHour(offering.getPricePerHour());
        response.setPricePerMonth(offering.getPricePerMonth());
        return response;
    }
}
