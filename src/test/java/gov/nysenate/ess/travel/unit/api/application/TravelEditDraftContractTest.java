package gov.nysenate.ess.travel.unit.api.application;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.DraftCtrl;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.draft.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class TravelEditDraftContractTest {
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
        DraftView patchResult = (DraftView) ((ViewObjectResponse<?>) new DraftCtrl()
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
}
