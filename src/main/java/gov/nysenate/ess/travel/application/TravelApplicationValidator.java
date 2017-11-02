package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.travel.addressvalidation.AddressValidationService;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelApplicationValidator {

    private AddressValidationService addressValidationService;

    @Autowired
    public TravelApplicationValidator(AddressValidationService addressValidationService) {
        this.addressValidationService = addressValidationService;
    }

    public void validateTravelApp(TravelApplication app) {
        validateItinerary(app);
        validateAllowances(app);
    }

    private void validateItinerary(TravelApplication app) {
        addressValidationService.validateAddress(app.getItinerary().getOrigin());
        for (TravelDestination td : app.getItinerary().getDestinations()) {
            addressValidationService.validateAddress(td.getAddress());
        }
    }


    private void validateAllowances(TravelApplication app) {

    }
    // set date times
        // validate itinerary
        //   - date times
        // calcualte allowances again.

}
