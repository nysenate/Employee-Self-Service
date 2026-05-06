package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class EverfiUserAttributesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void numericEmployeeId_isParsed() throws Exception {
        EverfiUserAttributes attributes = readAttributes("{\"employee_id\":\"123\"}");

        assertThat(attributes.getEmployeeId()).isEqualTo(123);
    }

    @Test
    public void blankEmployeeId_returnsNull() throws Exception {
        EverfiUserAttributes attributes = readAttributes("{\"employee_id\":\"\"}");

        assertThat(attributes.getEmployeeId()).isNull();
    }

    @Test
    public void missingEmployeeId_returnsNull() throws Exception {
        EverfiUserAttributes attributes = readAttributes("{}");

        assertThat(attributes.getEmployeeId()).isNull();
    }

    @Test
    public void nonNumericEmployeeId_returnsNull() throws Exception {
        EverfiUserAttributes attributes = readAttributes("{\"employee_id\":\"abc\"}");

        assertThat(attributes.getEmployeeId()).isNull();
    }

    private EverfiUserAttributes readAttributes(String json) throws Exception {
        return objectMapper.readValue(json, EverfiUserAttributes.class);
    }
}
