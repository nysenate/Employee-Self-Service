package gov.nysenate.ess.travel.unit.api.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.api.application.PurposeOfTravelView;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class PurposeOfTravelViewTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void newDraftPurposeSerializesWithEmptySummary() throws Exception {
        PurposeOfTravelView purpose = new PurposeOfTravelView(null);

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(purpose));

        assertThat(json.get("eventType").isNull()).isTrue();
        assertThat(json.get("summary").asText()).isEmpty();
    }
}
