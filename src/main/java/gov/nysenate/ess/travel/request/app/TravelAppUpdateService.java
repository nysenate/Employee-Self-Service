package gov.nysenate.ess.travel.request.app;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.notifications.email.events.TravelAppEditedEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelPendingReviewEmailEvent;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemService;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiemsFactory;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiem;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.draft.Draft;
import gov.nysenate.ess.travel.request.route.Leg;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.request.route.RouteService;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.review.strategy.ReviewerStrategy;
import gov.nysenate.ess.travel.review.strategy.ReviewerStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TravelAppUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(TravelAppUpdateService.class);

    @Autowired private RouteService routeService;
    @Autowired private MealPerDiemsFactory mealPerDiemsFactory;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private TravelEmailService emailService;
    @Autowired private EventBus eventBus;
    @Autowired private TravelRoleFactory travelRoleFactory;
    @Autowired private MileageAllowanceService mileageService;
    @Autowired private GsaAllowanceService gsaAllowanceService;
    @Autowired private ReviewerStrategyFactory reviewerStrategyFactory;
    @Autowired private LodgingPerDiemService lodgingPerDiemService;

    /**
     * Returns a new Amendment with the provided purpose of travel added to the amendment.
     *
     * @param amd The initial amendment.
     * @param pot The PurposeOfTravel to add to the amendment.
     * @return A new amendment which has been updated.
     */
//    public Amendment updatePurposeOfTravel(Amendment amd, PurposeOfTravel pot) {
//        return new Amendment.Builder(amd)
//                .withPurposeOfTravel(pot)
//                .build();
//    }

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
//    public Amendment updateOutboundRoute(Amendment amd, Route outboundRoute) {
//        if (!amd.route().equals(outboundRoute)) {
//             Don't override the route if nothing has changed.
//             That would delete the users returnLegs if they were already set.
//            amd = new Amendment.Builder(amd)
//                    .withRoute(outboundRoute)
//                    .build();
//        }
//        return amd;
//    }
    public void updateRoute(Draft draft) {
        Route fullRoute = routeService.createRoute(draft.getTravelApplication().getRoute());
        MileagePerDiems mileagePerDiems = createMileagePerDiems(fullRoute);
        MealPerDiems mealPerDiems = createMealPerDiems(fullRoute, draft.getTraveler());
        LodgingPerDiems lodgingPerDiems = lodgingPerDiemService.createLodgingPerDiems(fullRoute);
        draft.getTravelApplication().setRoute(fullRoute);
        draft.getTravelApplication().setMealPerDiems(mealPerDiems);
        draft.getTravelApplication().setMileagePerDiems(mileagePerDiems);
        draft.getTravelApplication().setLodgingPerDiems(lodgingPerDiems);
    }

    private MileagePerDiems createMileagePerDiems(Route route) {
        List<MileagePerDiem> mileagePerDiemList = new ArrayList<>();
        for (Leg leg : route.getAllLegs()) {
            double miles = mileageService.drivingDistance(leg.fromAddress(), leg.toAddress());
            BigDecimal mileageRate = mileageService.getIrsRate(leg.travelDate());
            PerDiem perDiem = new PerDiem(leg.travelDate(), mileageRate);
            mileagePerDiemList.add(new MileagePerDiem(0, leg.fromAddress(), leg.toAddress(), leg.getModeOfTransportation(),
                    miles, perDiem, leg.isOutbound(), true));
        }
        return new MileagePerDiems(mileagePerDiemList);
    }

    private MealPerDiems createMealPerDiems(Route route, TravelEmployee traveler) {
        return mealPerDiemsFactory.create(route, traveler);
    }

    /**
     * Returns a new Amendment with the provided allowances added to it.
     *
     * @param amd        The initial amendment.
     * @param allowances The allowances to add to the amendment.
     * @return A new Amendment with the allowances set.
     */
//    public Amendment updateAllowances(Amendment amd, Allowances allowances) {
//        return new Amendment.Builder(amd)
//                .withAllowances(allowances)
//                .build();
//    }

    /**
     * Returns a new Amendment with the provided meal per diems added to it.
     *
     * @param amd  The initial amendment.
     * @param mpds The MealPerDiems to add to the amendment.
     * @return A new Amendment with the MealPerDiems set.
     */
//    public Amendment updateMealPerDiems(Amendment amd, MealPerDiems mpds) {
//        return new Amendment.Builder(amd)
//                .withMealPerDiems(mpds)
//                .build();
//    }

    /**
     * Returns a new Amendment with the provided lodging per diems added to it.
     *
     * @param amd  The initial amendment.
     * @param lpds The LodgingPerDiems to add ot the amendment.
     * @return A new Amendment with the LodgingPerDiems set.
     */
//    public Amendment updateLodgingPerDiems(Amendment amd, LodgingPerDiems lpds) {
//        for (LodgingPerDiem lpd : lpds.allLodgingPerDiems()) {
//            if (lpd.rate().equals(Dollars.ZERO)) {
//                lpd.setRate(gsaAllowanceService.fetchLodgingRate(lpd.date(), lpd.address()));
//            }
//        }
//        return new Amendment.Builder(amd)
//                .withLodgingPerDiems(lpds)
//                .build();
//    }

    /**
     * Returns a new Amendment with the provided MileagePerDiems.
     *
     * @param amd  The initial amendment.
     * @param mpds The MileagePerDiems to reference when updating the Route.
     * @return The amd Amendment with the Route Legs `Leg.isReimbursementRequested` field updated.
     */
//    public Amendment updateMileagePerDiems(Amendment amd, MileagePerDiems mpds) {
//        return new Amendment.Builder(amd)
//                .withMileagePerDiems(mpds)
//                .build();
//    }

    /**
     * Persists the edits in {@code amd} to the application.
     *
     * @param appId The id of the TravelApplication to modify.
     * @param app   The edited travel application.
     * @param user  The logged in user who is making these changes.
     * @return
     */
    public TravelApplication editTravelApp(int appId, TravelApplication app, Employee user) {
        saveAppEdits(app, user);
        eventBus.post(new TravelAppEditedEmailEvent(app));
        return app;
    }

    private TravelApplication saveAppEdits(TravelApplication app, Employee user) {
        app.setModifiedBy(user);
        app.setModifiedDateTime(LocalDateTime.now());
        travelApplicationService.saveApplication(app);
        return app;
    }

    public TravelApplication resubmitApp(int appId, TravelApplication app, Employee user) {
        saveAppEdits(app, user);
        app.setStatus(new TravelApplicationStatus(getApprovalStatus(app)));
        travelApplicationService.saveApplication(app);
        ApplicationReview applicationReview = appReviewService.getApplicationReviewByAppId(app.getAppId());
        appReviewService.saveApplicationReview(applicationReview);
        eventBus.post(new TravelPendingReviewEmailEvent(applicationReview));
        return app;
    }

    /**
     * Creates and saves a new TravelApplication with one amendment {@code amd}.
     * This also creates and saves an ApplicationReview.
     */
    public TravelApplication submitTravelApplication(Draft draft, Employee submitter) {
        TravelApplication app = draft.getTravelApplication();
        app.setCreatedBy(submitter);
        app.setModifiedBy(submitter);
        app.setTravelerDeptHeadEmpId(draft.getTraveler().getDeptHeadId());
        app.setStatus(new TravelApplicationStatus(getApprovalStatus(app)));

        travelApplicationService.saveApplication(app);

        ApplicationReview appReview = appReviewService.createApplicationReview(app);
        appReviewService.saveApplicationReview(appReview);
        return app;
    }

    private AppStatus getApprovalStatus(TravelApplication app) {
        ReviewerStrategy reviewerStrategy = reviewerStrategyFactory.createStrategy(app);
        TravelRole roleToReview = reviewerStrategy.after(null);
        AppStatus appStatus;
        switch (roleToReview) {
            case TRAVEL_ADMIN, SECRETARY_OF_THE_SENATE, MAJORITY_LEADER -> appStatus = AppStatus.TRAVEL_UNIT;
            default -> appStatus = AppStatus.DEPARTMENT_HEAD;
        }
        return appStatus;
    }

}
