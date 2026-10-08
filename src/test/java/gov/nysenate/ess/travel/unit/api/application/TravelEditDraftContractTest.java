package gov.nysenate.ess.travel.unit.api.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.DraftCtrl;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.draft.*;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.EnumSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class TravelEditDraftContractTest {
    private final Subject subject = mock(Subject.class);
    private final DraftCtrl controller = new DraftCtrl() {
        @Override protected Subject getSubject() { return subject; }
    };

    @Test
    public void editDraftRoundTripsThroughActualJsonContractAndUnchangedExpensePatch() throws Exception {
        TravelApplication original = EditDraftFixture.application();
        Draft draft = new Draft(EditDraftFixture.CREATOR_ID, EditDraftFixture.traveler());
        draft.setTravelApplication(original);
        DraftView initialized = new DraftView(draft);
        assertNotNull("fixture traveler location", initialized.getTraveler().getEmpWorkLocation());

        String json = OutputUtils.jsonMapper.writeValueAsString(initialized);
        JsonNode wire = OutputUtils.jsonMapper.readTree(json);
        assertEquals(0, wire.get("id").asInt());
        assertFalse(wire.has("travelApplication"));
        assertEquals(EditDraftFixture.TRAVELER_ID, wire.get("traveler").get("employeeId").asInt());
        assertTrue("wire traveler location: " + wire.get("traveler"),
                wire.get("traveler").hasNonNull("empWorkLocation"));
        assertEquals(EditDraftFixture.CREATOR_ID, wire.get("amendment").get("createdBy").get("employeeId").asInt());
        assertEquals(EditDraftFixture.CREATED, java.time.LocalDateTime.parse(
                wire.get("amendment").get("createdDateTime").asText()));
        assertEquals(EditDraftFixture.ATTACHMENT_ID.toString(),
                wire.get("amendment").get("attachments").get(0).get("filename").asText());

        DraftView decoded = OutputUtils.jsonMapper.readValue(json, DraftView.class);
        assertNotNull("decoded traveler location", decoded.getTraveler().getEmpWorkLocation());
        TravelApplication restored = decoded.toDraft().getTravelApplication();
        assertEquals(0, restored.getAppId());
        assertEquals(EditDraftFixture.TRAVELER_ID, restored.getTraveler().getEmployeeId());
        assertEquals(EditDraftFixture.CREATOR_ID, restored.getCreatedBy().getEmployeeId());
        assertEquals(EditDraftFixture.CREATED, restored.getCreatedDateTime());
        assertEquals(original.getPurposeOfTravel(), restored.getPurposeOfTravel());
        assertEquals(original.getRoute(), restored.getRoute());
        assertEquals(original.getAllowances(), restored.getAllowances());
        assertEquals(original.getAttachments(), restored.getAttachments());
        assertEquals(original.getMealPerDiems(), restored.getMealPerDiems());
        assertEquals(original.getLodgingPerDiems(), restored.getLodgingPerDiems());
        assertEquals(40.0, restored.getMileagePerDiems().allPerDiems().get(0).getMiles(), 0.0);
        assertEquals(1, restored.getMileagePerDiems().requestedPerDiems().size());

        DraftViewPatches patches = new DraftViewPatches();
        patches.setDraft(decoded);
        patches.setOptions(EnumSet.of(DraftViewPatchOption.ALLOWANCES,
                DraftViewPatchOption.MEAL_PER_DIEMS, DraftViewPatchOption.LODGING_PER_DIEMS,
                DraftViewPatchOption.MILEAGE_PER_DIEMS));
        DraftViewPatches wirePatches = OutputUtils.jsonMapper.readValue(
                OutputUtils.jsonMapper.writeValueAsString(patches), DraftViewPatches.class);
        DraftView patchResult = (DraftView) ((ViewObjectResponse<?>) controller
                .patchDraftApp(wirePatches)).result;
        DraftView decodedResult = OutputUtils.jsonMapper.readValue(
                OutputUtils.jsonMapper.writeValueAsString(patchResult), DraftView.class);
        TravelApplication patched = decodedResult.toDraft().getTravelApplication();
        assertEquals(restored.getPurposeOfTravel(), patched.getPurposeOfTravel());
        assertEquals(restored.getRoute(), patched.getRoute());
        assertEquals(restored.getAllowances(), patched.getAllowances());
        assertEquals(restored.getAttachments(), patched.getAttachments());
        assertEquals(restored.getMealPerDiems(), patched.getMealPerDiems());
        assertEquals(restored.getLodgingPerDiems(), patched.getLodgingPerDiems());
        assertEquals(restored.getMileagePerDiems().allPerDiems().get(0).getMiles(),
                patched.getMileagePerDiems().allPerDiems().get(0).getMiles(), 0.0);
        assertEquals(restored.getMileagePerDiems().requestedPerDiems().size(),
                patched.getMileagePerDiems().requestedPerDiems().size());
        assertEquals(restored.getCreatedBy().getEmployeeId(), patched.getCreatedBy().getEmployeeId());
        assertEquals(restored.getCreatedDateTime(), patched.getCreatedDateTime());
    }
    @Test
    public void expensePatchUsesOverrideTotalsAndZeroRestoresCalculatedAmounts() throws Exception {
        Draft draft = new Draft(EditDraftFixture.CREATOR_ID, EditDraftFixture.traveler());
        draft.setTravelApplication(EditDraftFixture.application());
        ObjectNode wire = OutputUtils.jsonMapper.valueToTree(new DraftView(draft));
        ObjectNode meals = (ObjectNode) wire.path("amendment").path("mealPerDiems");
        ObjectNode lodging = (ObjectNode) wire.path("amendment").path("lodgingPerDiems");
        meals.put("overrideRate", 123.45);
        lodging.put("overrideRate", 678.90);
        TravelApplication overridden = patch(wire);
        assertEquals("123.45", overridden.getMealPerDiems().total().toString());
        assertEquals("678.90", overridden.getLodgingPerDiems().totalPerDiem().toString());

        meals.put("overrideRate", 0);
        meals.put("isOverridden", true); // The amount, not this display flag, controls the override.
        lodging.put("overrideRate", 0);
        lodging.put("isOverridden", true);
        TravelApplication restored = patch(wire);
        assertEquals(draft.getTravelApplication().getMealPerDiems().total(), restored.getMealPerDiems().total());
        assertEquals(draft.getTravelApplication().getLodgingPerDiems().totalPerDiem(),
                restored.getLodgingPerDiems().totalPerDiem());
        assertFalse(restored.getLodgingPerDiems().isOverridden());
    }

    private TravelApplication patch(ObjectNode wire) throws Exception {
        DraftViewPatches patches = new DraftViewPatches();
        patches.setDraft(OutputUtils.jsonMapper.treeToValue(wire, DraftView.class));
        patches.setOptions(EnumSet.of(DraftViewPatchOption.MEAL_PER_DIEMS, DraftViewPatchOption.LODGING_PER_DIEMS));
        DraftView result = (DraftView) ((ViewObjectResponse<?>) controller.patchDraftApp(patches)).result;
        return result.toDraft().getTravelApplication();
    }

}
