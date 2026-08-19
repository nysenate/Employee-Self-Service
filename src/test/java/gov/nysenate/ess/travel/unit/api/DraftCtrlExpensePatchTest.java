package gov.nysenate.ess.travel.unit.api;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Gender;
import gov.nysenate.ess.travel.api.DraftCtrl;
import gov.nysenate.ess.travel.department.Department;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.fixtures.TravelAddressFixture;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiem;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.draft.Draft;
import gov.nysenate.ess.travel.request.draft.DraftView;
import gov.nysenate.ess.travel.request.draft.DraftViewPatchOption;
import gov.nysenate.ess.travel.request.draft.DraftViewPatches;
import gov.nysenate.ess.travel.request.route.ModeOfTransportation;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class DraftCtrlExpensePatchTest {

    private static final LocalDate TRAVEL_DATE = LocalDate.of(2026, 8, 19);

    @Test
    public void expensePatchRetainsSelectionsRecalculatesTotalsAndDoesNotPersist() throws Exception {
        Draft draft = expenseDraft();
        DraftView submittedView = new DraftView() {
            @Override
            public Draft toDraft() {
                return draft;
            }
        };

        DraftViewPatches patches = new DraftViewPatches();
        patches.setDraft(submittedView);
        patches.setOptions(EnumSet.of(
                DraftViewPatchOption.ALLOWANCES,
                DraftViewPatchOption.MEAL_PER_DIEMS,
                DraftViewPatchOption.LODGING_PER_DIEMS,
                DraftViewPatchOption.MILEAGE_PER_DIEMS
        ));

        DraftCtrl controller = new DraftCtrl();

        BaseResponse response = controller.patchDraftApp(patches);
        DraftView result = (DraftView) ((ViewObjectResponse<?>) response).result;

        assertEquals(1.0, result.getAmendment().getAllowances().getTolls(), 0.0);
        assertEquals(2.0, result.getAmendment().getAllowances().getParking(), 0.0);
        assertEquals(3.0, result.getAmendment().getAllowances().getAlternateTransportation(), 0.0);
        assertEquals(4.0, result.getAmendment().getAllowances().getTrainAndPlane(), 0.0);
        assertEquals(5.0, result.getAmendment().getAllowances().getRegistration(), 0.0);

        assertFalse(result.getAmendment().getMealPerDiems().getAllMealPerDiems().get(0).isBreakfastRequested());
        assertTrue(result.getAmendment().getMealPerDiems().getAllMealPerDiems().get(0).isDinnerRequested());
        assertFalse(result.getAmendment().getLodgingPerDiems().getAllLodgingPerDiems().get(0).isReimbursementRequested());
        assertTrue(result.getAmendment().getMileagePerDiems().getAllPerDiems().get(0).isReimbursementRequested());

        assertEquals("20.00", result.getAmendment().getMealAllowance());
        assertEquals("0.00", result.getAmendment().getLodgingAllowance());
        assertEquals("20.00", result.getAmendment().getMileageAllowance());
        assertEquals("55.00", result.getAmendment().getTotalAllowance());
    }

    private Draft expenseDraft() {
        Employee employee = new Employee();
        employee.setEmployeeId(123);
        employee.setGender(Gender.M);
        TravelEmployee traveler = new TravelEmployee(
                employee,
                new Department(employee, Collections.emptySet())
        );
        Draft draft = new Draft(employee.getEmployeeId(), traveler);

        Allowances allowances = new Allowances();
        allowances.setTolls(new Dollars("1.00"));
        allowances.setParking(new Dollars("2.00"));
        allowances.setAlternateTransportation(new Dollars("3.00"));
        allowances.setTrainAndPlane(new Dollars("4.00"));
        allowances.setRegistration(new Dollars("5.00"));

        SenateMie mie = new SenateMie(
                0,
                TRAVEL_DATE.getYear(),
                new Dollars("30.00"),
                new Dollars("10.00"),
                new Dollars("20.00")
        );
        MealPerDiem meal = new MealPerDiem(
                0,
                TravelAddressFixture.albany(),
                TRAVEL_DATE,
                new Dollars("30.00"),
                mie,
                false,
                true,
                true,
                true
        );
        LodgingPerDiem lodging = new LodgingPerDiem(
                0,
                TravelAddressFixture.albany(),
                new PerDiem(TRAVEL_DATE, new Dollars("100.00")),
                false
        );
        MileagePerDiem mileage = new MileagePerDiem(
                0,
                TravelAddressFixture.albany(),
                TravelAddressFixture.nyc(),
                ModeOfTransportation.PERSONAL_AUTO,
                40.0,
                new PerDiem(TRAVEL_DATE, new Dollars("0.50")),
                true,
                true
        );

        TravelApplication application = draft.getTravelApplication();
        application.setAllowances(allowances);
        MealPerDiems meals = new MealPerDiems(Collections.singleton(meal));
        Object mealAdjustments = ReflectionTestUtils.getField(meals, "adjustments");
        ReflectionTestUtils.setField(mealAdjustments, "isAllowedMeals", true);
        application.setMealPerDiems(meals);
        application.setLodgingPerDiems(new LodgingPerDiems(Collections.singleton(lodging)));
        application.setMileagePerDiems(new MileagePerDiems(Collections.singleton(mileage)));
        return draft;
    }
}
