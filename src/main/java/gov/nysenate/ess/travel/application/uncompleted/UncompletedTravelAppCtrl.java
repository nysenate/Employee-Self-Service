package gov.nysenate.ess.travel.application.uncompleted;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.accommodation.*;
import gov.nysenate.ess.travel.application.*;
import gov.nysenate.ess.travel.route.Route;
import gov.nysenate.ess.travel.route.RouteFactory;
import gov.nysenate.ess.travel.utils.UploadProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application/uncompleted")
public class UncompletedTravelAppCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private InMemoryTravelAppDao appDao;
    @Autowired private UploadProcessor uploadProcessor;
    @Autowired private AccommodationFactory accommodationFactory;
    @Autowired private RouteFactory routeFactory;

    /**
     * Initialize a mostly empty travel app, containing just the traveling {@link Employee}
     * and the submitter {@link Employee}
     * <p>
     * (POST) /api/v1/travel/application/uncompleted/init
     * <p>
     * Request Params: empId (int) - The travelers employee id.
     *
     * @param empId
     * @return An application with the traveler and submitter initialized.
     */
    @RequestMapping(value = "/init", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse initTravelApplication(@RequestParam int empId) {
        Employee traveler = employeeInfoService.getEmployee(empId);
        Employee submitter = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelApplicationView uncompletedApp = appDao.getUncompletedAppByEmpId(traveler.getEmployeeId());
        if (uncompletedApp == null) {
            uncompletedApp = new TravelApplicationView(traveler, submitter);
            uncompletedApp = appDao.saveUncompleteTravelApp(uncompletedApp);
        }
        return new ViewObjectResponse<>(uncompletedApp);
    }

    /**
     * Saves a purpose to an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/purpose
     * <p>
     *     Path Params: id (int) - The id of an uncompleted travel application to update with the given purpose.<br>
     *     Body : The purpose of travel.
     *
     * @param id
     * @param purpose
     * @return The application with its purpose updated.
     */
    @RequestMapping(value = "/{id}/purpose", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse savePurpose(@PathVariable int id, @RequestBody String purpose) {
        TravelApplicationView app = appDao.getUncompletedAppById(id);
        app.setPurposeOfTravel(purpose);
        app = appDao.saveUncompleteTravelApp(app);
        return new ViewObjectResponse<>(app);
    }

    /**
     * Saves the outbound segments of an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/outbound
     * <p>
     *     Path Params: id (int) - The id of an uncompleted travel application to update with the given outbound segments.<br>
     *     Body : An array of SegmentView's
     */
    @RequestMapping(value = "/{id}/outbound", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveOutboundSegments(@PathVariable int id, @RequestBody SegmentView[] segments) {
        TravelApplicationView app = appDao.getUncompletedAppById(id);
        app.setOutboundSegments(Arrays.asList(segments));
        app = appDao.saveUncompleteTravelApp(app);
        return new ViewObjectResponse<>(app);
    }

    /**
     * Saves the return segments of an uncompleted application and calculates the Route and Accommodations.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/return
     * <p>
     *     Path Params: id (int) - The id of an uncompleted travel application to update with the given return segments.<br>
     *     Body : An array of SegmentView's
     */
    @RequestMapping(value = "/{id}/return", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveReturnSegments(@PathVariable int id, @RequestBody SegmentView[] segments) throws IOException, ApiException, InterruptedException {
        TravelApplicationView appView = appDao.getUncompletedAppById(id);
        appView.setReturnSegments(Arrays.asList(segments));

        List<Accommodation> accommodations = accommodationFactory.createAccommodations(appView);
        Route route = routeFactory.createRoute(appView);

        TravelApplication app = appView.toTravelApplication();
        app.setAccommodations(accommodations);
        app.setRoute(route);
        TravelApplicationView updatedView = new TravelApplicationView(app); // This updates the app view with fields calculated by the TravelApplication object.
        // Still need the outbound and return segments
        updatedView.setOutboundSegments(appView.getOutboundSegments());
        updatedView.setReturnSegments(appView.getReturnSegments());
        updatedView = appDao.saveUncompleteTravelApp(updatedView);
        return new ViewObjectResponse<>(updatedView);
    }

    @RequestMapping(value = "/{id}/expenses", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveExpenses(@PathVariable int id, @RequestBody ExpensesDtoView expensesDto) {
        TravelApplicationView appView = appDao.getUncompletedAppById(id);
        appView.setTollsAllowance(expensesDto.allowances.tollsAllowance);
        appView.setParkingAllowance(expensesDto.allowances.parkingAllowance);
        appView.setAlternateAllowance(expensesDto.allowances.alternateAllowance);
        appView.setRegistrationAllowance(expensesDto.allowances.registrationAllowance);
        // errrmmmm
        for (DestinationDtoView dest : expensesDto.getDestinations()) {
            for (AccommodationView accommodation : appView.getAccommodations()) {
                if (accommodation.getAddress().getFormattedAddress().equals(dest.address.getFormattedAddress())) {
                    for (StayDtoView stay : dest.getStays()) {
                        for (DayView day : accommodation.getDays()) {
                            if (day.getDate().equals(stay.getDate())) {
                                day.isMealsRequested = stay.isMealsRequested;
                            }
                        }
                        for (NightView night : accommodation.getNights()) {
                            if (night.getDate().equals(stay.getDate())) {
                                night.isLodgingRequested = stay.isLodgingRequested;
                            }
                        }
                    }
                }
            }
        }

        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }

    /**
     * File upload test endpoint
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse upload(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            TravelAttachment attach = uploadProcessor.uploadTravelAttachment(file);
            System.out.println("");
        }
        return new SimpleResponse();
    }


    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
