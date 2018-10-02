package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.provider.dod.DodAllowanceService;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class ServiceProviderFactory {

    private GsaAllowanceService gsaAllowanceService;

    private DodAllowanceService dodAllowanceService;

    @Autowired
    public ServiceProviderFactory(GsaAllowanceService gsaAllowanceService, DodAllowanceService dodAllowanceService) {
        this.gsaAllowanceService = gsaAllowanceService;
        this.dodAllowanceService = dodAllowanceService;
    }


    public Dollars fetchMealRate(LocalDate date, Address address) throws IOException {
        if (determineDOD(address.getCountry(), address.getState())) {
            return dodAllowanceService.fetchMealRate( determineDODLocationString(address.getCountry(), address.getState()), address.getCity(), date );
        }
        else {
            return gsaAllowanceService.fetchMealRate(date, address);
        }
    }

    public Dollars fetchLodgingRate(LocalDate date, Address address) throws IOException {
        if (determineDOD(address.getCountry(), address.getState())) {
            return dodAllowanceService.fetchLodgingRate( determineDODLocationString(address.getCountry(), address.getState()), address.getCity(), date );
        }
        else {
            return gsaAllowanceService.fetchLodgingRate(date, address);
        }
    }

    private boolean determineDOD(String country, String state) {
        if (!country.equals("United States") || state.equalsIgnoreCase("Hawaii")
                || state.equalsIgnoreCase("Alaska")) {
            return true;
        }
        return false;
    }

    private String determineDODLocationString(String country, String state) {
        if ( state.equalsIgnoreCase("Hawaii") ) {
            return "Hawaii";
        }
        else if ( state.equalsIgnoreCase("Alaska") ) {
            return "Alaska";
        }
        else {
            return country;
        }
    }
}
