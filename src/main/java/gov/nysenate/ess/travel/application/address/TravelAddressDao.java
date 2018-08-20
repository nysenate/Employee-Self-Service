package gov.nysenate.ess.travel.application.address;

import java.util.List;

public interface TravelAddressDao {

    void insertAddresses(List<TravelAddress> addresses);
}
