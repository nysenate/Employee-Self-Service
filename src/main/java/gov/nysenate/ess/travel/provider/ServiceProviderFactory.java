package gov.nysenate.ess.travel.provider;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.util.UnsuccessfulHttpReqException;
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

    /**
     * Gets the meal rate in Dollars for a given date and address.
     * Queries one of multiple 3rd party providers to get the meal rate value.
     * @param date The date of this meal rate.
     * @param address The address of this meal rate.
     * @return {@link Dollars} representing the meal rate value for this address and date.
     * @throws ProviderException If there is an error communicating with our 3rd party meal rate providers.
     */
    public Dollars fetchMealRate(LocalDate date, Address address) {
        Dollars mealRate;
        try {
            if (determineDOD(address.getCountry(), address.getState())) {
                mealRate = dodAllowanceService.fetchMealRate(determineDODLocationString(address.getCountry(), address.getState()), address.getCity(), date);
            } else {
                mealRate = gsaAllowanceService.fetchMealRate(date, address);
            }
        }
        catch (IOException | UnsuccessfulHttpReqException ex) {
            throw new ProviderException(ex);
        }
        return mealRate;
    }

    /**
     * Calculates the lodging rate for the given address and date.
     * @param date The date the lodging will occur.
     * @param address The address where lodging will occur.
     * @return {@link Dollars} representing the lodging rate for this address and date.
     * @throws ProviderException If there is an error communicating with our 3rd party meal rate providers.
     */
    public Dollars fetchLodgingRate(LocalDate date, Address address) {
        Dollars rate;
        try {
            if (determineDOD(address.getCountry(), address.getState())) {
                rate = dodAllowanceService.fetchLodgingRate(determineDODLocationString(address.getCountry(), address.getState()), address.getCity(), date);
            } else {
                rate = gsaAllowanceService.fetchLodgingRate(date, address);
            }
        }
        catch (IOException | UnsuccessfulHttpReqException ex) {
            throw new ProviderException(ex);
        }
        return rate;
    }

    private boolean determineDOD(String country, String state) {
        if (!country.equals("United States") || state.equalsIgnoreCase("Hawaii")
                || state.equalsIgnoreCase("Alaska")) {
            return true;
        }
        return false;
    }

    private String determineDODLocationString(String country, String state) {
        if (state.equalsIgnoreCase("Hawaii")) {
            return "Hawaii";
        } else if (state.equalsIgnoreCase("Alaska")) {
            return "Alaska";
        } else {
            return country;
        }
    }
}
