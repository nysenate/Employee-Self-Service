package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.provider.ServiceProviderFactory;
import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LodgingAllowancesFactory {

    private ServiceProviderFactory serviceProviderFactory;

    @Autowired
    public LodgingAllowancesFactory(ServiceProviderFactory serviceProviderFactory) {
        this.serviceProviderFactory = serviceProviderFactory;
    }

    /**
     * Crates a {@link LodgingAllowances} from {@link Destinations}.
     * Sums up the lodging rates for each over night destination.
     *
     * @param destinations
     * @return A initialized {@link LodgingAllowances}.
     * @throws ProviderException If there is an error communicating with our 3rd party meal rate providers.
     */
    public LodgingAllowances createLodgingAllowances(Destinations destinations) {
        List<LodgingAllowance> lodgingAllowances = new ArrayList<>();

        for (Destination dest : destinations.getDestinations()) {
            for (LocalDate night : dest.nights()) {
                Dollars lodgingAllowance = serviceProviderFactory.fetchLodgingRate(night, dest.getAddress());
                lodgingAllowances.add(new LodgingAllowance(UUID.randomUUID(), dest.getAddress(), night, lodgingAllowance, true));
            }
        }
        return new LodgingAllowances(lodgingAllowances);
    }
}
