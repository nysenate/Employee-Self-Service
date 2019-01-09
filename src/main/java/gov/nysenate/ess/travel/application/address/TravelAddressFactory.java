package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TravelAddressFactory {

    private static TravelAddressDao travelAddressDao;

    @Autowired
    public TravelAddressFactory(TravelAddressDao travelAddressDao) {
        TravelAddressFactory.travelAddressDao = travelAddressDao;
    }

    /**
     * TravelAddress id's should always be the same for a particular Address.
     * This ensures multiple TravelAddressViews created at the same time for the same address get assigned the same id.
     *
     * @param address
     * @return
     */
    public synchronized static TravelAddress createTravelAddress(Address address) {
        if (travelAddressDao.doesAddressExist(address)) {
            return travelAddressDao.selectAddress(address);
        } else {
            TravelAddress ta = new TravelAddress(UUID.randomUUID(), address);
            ta.setCounty(address.getCounty());
            travelAddressDao.insertAddress(ta);
            return ta;
        }
    }
}
