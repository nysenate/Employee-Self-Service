package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TravelAddressService {

    private static TravelAddressDao travelAddressDao;

    @Autowired
    public TravelAddressService(TravelAddressDao travelAddressDao) {
        TravelAddressService.travelAddressDao = travelAddressDao;
    }

    public synchronized static TravelAddress createTravelAddress(Address address) {
        if (travelAddressDao.doesAddressExist(address)) {
            return travelAddressDao.selectAddress(address);
        }
        else {
            TravelAddress ta = new TravelAddress(UUID.randomUUID(), address);
            ta.setCounty(address.getCounty());
            travelAddressDao.insertAddress(ta);
            return ta;
        }
    }
}
