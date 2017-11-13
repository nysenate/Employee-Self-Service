package gov.nysenate.ess.travel.maps;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.List;

public interface MapInterface {

    public TripDistance getTripDistance(List<Address> travelRoute) throws Exception;
}
