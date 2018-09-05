package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.List;
import java.util.UUID;

public interface TravelAddressDao {

    void insertAddress(TravelAddress address);

    TravelAddress selectAddress(Address address);

    boolean doesAddressExist(Address address);
}
