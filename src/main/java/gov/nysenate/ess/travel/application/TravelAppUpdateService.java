package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteService;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SqlSenateMieDao;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class TravelAppUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(TravelAppUpdateService.class);

    @Autowired private RouteService routeService;
    @Autowired private SqlSenateMieDao senateMieDao;
    @Autowired private TravelApplicationService appService;
    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private TravelEmailService emailService;

    /**
     * Returns a new Amendment with the provided purpose of travel added to the amendment.
     *
     * @param amd The initial amendment.
     * @param pot The PurposeOfTravel to add to the amendment.
     * @return A new amendment which has been updated.
     */
    public Amendment updatePurposeOfTravel(Amendment amd, PurposeOfTravel pot) {
        return new Amendment.Builder(amd)
                .withPurposeOfTravel(pot)
                .build();
    }

    /**
     * Returns a new Amendment with the provided outbound route added to it.
     * <p>
     * This is used when editing a TravelApp to save the outbound route data
     * before we have a full route.
     *
     * @param amd           The initial amendment.
     * @param outboundRoute A Route which only contains outbound leg information.
     * @return A new amendment which has been updated. Or {@code amd} if the
     * outbound legs have not been updated.
     */
    public Amendment updateOutboundRoute(Amendment amd, Route outboundRoute) {
        if (!amd.route().equals(outboundRoute)) {
            // Don't override the route if nothing has changed.
            // That would delete the users returnLegs if they were already set.
            amd = new Amendment.Builder(amd)
                    .withRoute(outboundRoute)
                    .build();
        }
        return amd;
    }

    /**
     * Returns a new Amendment with the provided route calculated and set on the returned amendment.
     * This also calculates the MealPerDiems and LodgingPerDiems based on the route and sets them
     * on the returned amendment.
     * <p>
     * Use this once both outbound and return legs have been added to the route so that
     * milage, per diems, etc can be calculated.
     *
     * @param amd   The initial amendment.
     * @param route A Route with both outbound and return legs set. All Route data i.e. mileage, per diems
     *              will be calculated and set on the Route. This Route will be added to the returned amendment.
     * @return A new Amendment with the full calculated Route added to it.
     */
    public Amendment updateRoute(Amendment amd, Route route) {
        Route fullRoute = routeService.createRoute(route);
        MealPerDiems mpds = createMealPerDiems(fullRoute);
        LodgingPerDiems lpds = createLodgingPerDiems(fullRoute);
        return new Amendment.Builder(amd)
                .withRoute(fullRoute)
                .withMealPerDiems(mpds)
                .withLodgingPerDiems(lpds)
                .build();
    }

    private MealPerDiems createMealPerDiems(Route route) {
        Set<MealPerDiem> mealPerDiemSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (PerDiem pd : d.mealPerDiems()) {
                // Ignore Per Diem if the rate is zero - there is no meal per diem.
                if (!pd.isRateZero()) {
                    SenateMie mie = null;
                    try {
                        mie = senateMieDao.selectSenateMie(DateUtils.getFederalFiscalYear(pd.getDate()), new Dollars(pd.getRate()));
                    } catch (IncorrectResultSizeDataAccessException ex) {
                        logger.warn("Unable to find Senate mie for date: " + pd.getDate().toString() + " and total: " + pd.getRate().toString());
                    }
                    mealPerDiemSet.add(new MealPerDiem(d.getAddress(), pd.getDate(), new Dollars(pd.getRate()), mie));
                }
            }
        }
        return new MealPerDiems(mealPerDiemSet);
    }

    private LodgingPerDiems createLodgingPerDiems(Route route) {
        Set<LodgingPerDiem> lodgingPerDiemSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (PerDiem pd : d.lodgingPerDiems()) {
                // Ignore Per Diem if the rate is zero - there is no lodging per diem.
                if (!pd.isRateZero()) {
                    lodgingPerDiemSet.add(new LodgingPerDiem(d.getAddress(), pd));
                }
            }
        }
        return new LodgingPerDiems(lodgingPerDiemSet);
    }

    /**
     * Returns a new Amendment with the provided allowances added to it.
     *
     * @param amd        The initial amendment.
     * @param allowances The allowances to add to the amendment.
     * @return A new Amendment with the allowances set.
     */
    public Amendment updateAllowances(Amendment amd, Allowances allowances) {
        return new Amendment.Builder(amd)
                .withAllowances(allowances)
                .build();
    }

    /**
     * Returns a new Amendment with the provided meal per diems added to it.
     *
     * @param amd  The initial amendment.
     * @param mpds The MealPerDiems to add to the amendment.
     * @return A new Amendment with the MealPerDiems set.
     */
    public Amendment updateMealPerDiems(Amendment amd, MealPerDiems mpds) {
        return new Amendment.Builder(amd)
                .withMealPerDiems(mpds)
                .build();
    }

    /**
     * Returns a new Amendment with the provided lodging per diems added to it.
     *
     * @param amd  The initial amendment.
     * @param lpds The LodgingPerDiems to add ot the amendment.
     * @return A new Amendment with the LodgingPerDiems set.
     */
    public Amendment updateLodgingPerDiems(Amendment amd, LodgingPerDiems lpds) {
        return new Amendment.Builder(amd)
                .withLodgingPerDiems(lpds)
                .build();
    }
    /**
     * Updates the mileage per diem information on the amendment's Route.
     * This allows users to opt in or out of mileage reimbursement for each leg of their trip.
     *
     * @param amd  The initial amendment.
     * @param mpds The MileagePerDiems to reference when updating the Route.
     * @return The amd Amendment with the Route Legs `Leg.isReimbursementRequested` field updated.
     */
    public Amendment updateMileagePerDiems(Amendment amd, MileagePerDiems mpds) {
        for (Leg qualifyingLeg : mpds.mileageReimbursableLegs()) {
            for (Leg appLeg : amd.route().getAllLegs()) {
                if (appLeg.fromAddress().equals(qualifyingLeg.fromAddress())
                        && appLeg.toAddress().equals(qualifyingLeg.toAddress())
                        && appLeg.travelDate().equals(qualifyingLeg.travelDate())) {
                    appLeg.setIsReimbursementRequested(qualifyingLeg.isReimbursementRequested());
                }
            }
        }
        return amd;
    }

    /**
     * Persists the edits in {@code amd} to the application.
     * @param appId The id of the TravelApplication to modify.
     * @param amd An amendment with all desired changes made to it.
     * @param user The logged in user who is making these changes.
     * @return
     */
    public TravelApplication saveAppEdits(int appId, Amendment amd, Employee user) {
        amd = new Amendment.Builder(amd)
                .withAmendmentId(0)
                .withCreatedBy(user)
                .withCreatedDateTime(LocalDateTime.now())
                .build();

        TravelApplication app = appService.getTravelApplication(appId);
        app.addAmendment(amd);
        appService.saveApplication(app);
        emailService.sendEditEmails(app);
        return app;
    }

    /**
     * Creates and saves a new TravelApplication with one amendment {@code amd}.
     * This also creates and saves an ApplicationReview.
     *
     * @param amd The first amendment to the TravelApplication
     * @param traveler The employee who will be traveling.
     * @param submitter The employee who is submitting the application.
     * @return
     */
    public TravelApplication submitTravelApplication(Amendment amd, Employee traveler, Employee submitter) {
        amd = new Amendment.Builder(amd)
                .withAmendmentId(0)
                .withVersion(Version.A)
                .withCreatedBy(submitter)
                .withCreatedDateTime(LocalDateTime.now())
                .build();

        TravelApplication app = new TravelApplication(traveler, amd);
        appService.saveApplication(app);

        ApplicationReview appReview = appReviewService.createApplicationReview(app);
        appReviewService.saveApplicationReview(appReview);
        return app;
    }
}
