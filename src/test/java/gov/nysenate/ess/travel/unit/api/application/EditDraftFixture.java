package gov.nysenate.ess.travel.unit.api.application;

import gov.nysenate.ess.core.model.personnel.*;
import gov.nysenate.ess.core.model.unit.*;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.department.Department;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.fixtures.TravelAddressFixture;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.*;
import gov.nysenate.ess.travel.request.allowances.meal.*;
import gov.nysenate.ess.travel.request.allowances.mileage.*;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.route.*;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

final class EditDraftFixture {
    static final int APP_ID = 411;
    static final int TRAVELER_ID = 112;
    static final int CREATOR_ID = 113;
    static final UUID ATTACHMENT_ID = UUID.fromString("7ba7213e-57f5-49e1-bff3-bb02f5c25cc1");
    static final LocalDate DATE = LocalDate.of(2026, 8, 19);
    static final LocalDateTime CREATED = LocalDateTime.of(2026, 7, 1, 9, 30);

    private EditDraftFixture() {}

    static Employee employee(int id) {
        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setGender(Gender.M);
        ResponsibilityHead responsibilityHead = new ResponsibilityHead();
        ResponsibilityCenter center = new ResponsibilityCenter();
        center.setCode(300);
        center.setHead(responsibilityHead);
        employee.setRespCenter(center);
        employee.setWorkLocation(new Location(new LocationId("A42FB", 'W'),
                new Address("100 State Street"), responsibilityHead, "Office", true));
        return employee;
    }

    static TravelEmployee traveler() {
        Employee employee = employee(TRAVELER_ID);
        return new TravelEmployee(employee, new Department(employee(700), Set.of()));
    }

    static TravelApplication application() {
        TravelEmployee traveler = traveler();
        Allowances allowances = new Allowances();
        allowances.setTolls(new Dollars("1.00"));
        allowances.setParking(new Dollars("2.00"));
        allowances.setAlternateTransportation(new Dollars("3.00"));
        allowances.setTrainAndPlane(new Dollars("4.00"));
        allowances.setRegistration(new Dollars("5.00"));
        Destination nyc = new Destination(TravelAddressFixture.nyc(), DATE, DATE);
        Destination albany = new Destination(TravelAddressFixture.albany(), DATE, DATE);
        Route route = new Route(
                List.of(new Leg(1, nyc, albany, ModeOfTransportation.PERSONAL_AUTO, true, DATE)),
                List.of(new Leg(2, albany, nyc, ModeOfTransportation.TRAIN, false, DATE)), true, true);
        MealPerDiem meal = new MealPerDiem(0, TravelAddressFixture.albany(), DATE,
                new Dollars("30.00"), new gov.nysenate.ess.travel.provider.senate.SenateMie(
                0, DATE.getYear(), new Dollars("30.00"), new Dollars("10.00"), new Dollars("20.00")),
                false, true, true, true);
        MealPerDiems meals = new MealPerDiems(Set.of(meal));
        Object adjustments = ReflectionTestUtils.getField(meals, "adjustments");
        ReflectionTestUtils.setField(adjustments, "isAllowedMeals", true);
        return new TravelApplication.Builder(traveler, 700)
                .withAppId(APP_ID)
                .withCreatedBy(employee(CREATOR_ID))
                .withCreatedDateTime(CREATED)
                .withStatus(new TravelApplicationStatus(AppStatus.DISAPPROVED))
                .withPurposeOfTravel(new PurposeOfTravel(EventType.PUBLIC_HEARING, "Budget hearing", "Travel"))
                .withRoute(route)
                .withAllowances(allowances)
                .withMealPerDiems(meals)
                .withLodgingPerDiems(new LodgingPerDiems(Set.of(new LodgingPerDiem(0,
                        TravelAddressFixture.albany(), new PerDiem(DATE, new Dollars("100.00")), false))))
                .withMileagePerDiems(new MileagePerDiems(Set.of(new MileagePerDiem(0,
                        TravelAddressFixture.albany(), TravelAddressFixture.nyc(),
                        ModeOfTransportation.PERSONAL_AUTO, 40.0,
                        new PerDiem(DATE, new Dollars("0.50")), true, true))))
                .withAttachments(List.of(new Attachment(ATTACHMENT_ID, "receipt.pdf", "application/pdf")))
                .build();
    }
}
