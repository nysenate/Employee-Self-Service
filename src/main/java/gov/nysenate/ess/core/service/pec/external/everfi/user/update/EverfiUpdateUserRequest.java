package gov.nysenate.ess.core.service.pec.external.everfi.user.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.List;

public class EverfiUpdateUserRequest {

    private static final String UPDATE_USER_END_POINT = "/v1/admin/registration_sets/%s";
    private final EverfiApiClient everfiClient;

    private final String empUuid;
    private final String firstName; // Required, even if not changing.
    private final String lastName; // Required, even if not changing.
    private final String email; // Required, even if not changing.
    private final String ssoId;
    private final int employeeId;
    private static final int LOCATION_ID = 9820; // We put everyone in the same location.
    private final List<EverfiCategoryLabel> categoryLabels;
    private final boolean isActive;

    // TODO builder pattern?
    public EverfiUpdateUserRequest(EverfiApiClient everfiClient, String empUuid, int employeeId, String firstName, String lastName,
                                   String email, String ssoId, List<EverfiCategoryLabel> categoryLabels, boolean isActive) {
        this.everfiClient = everfiClient;
        this.empUuid = empUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ssoId = ssoId;
        this.employeeId = employeeId;
        this.categoryLabels = categoryLabels;
        this.isActive = isActive;
    }

    /**
     * Updates the user represented by the feilds in this class.
     * @return The updated user from Everfi.
     * @throws IOException
     */
    public EverfiUser updateUser() throws IOException {
        String entity = generateJsonEntity();
        String data = everfiClient.patch(endpoint(), entity);

        ObjectMapper mapper = OutputUtils.jsonMapper;
        JsonNode rootNode = mapper.readTree(data);
        JsonNode emp = rootNode.get("data");
        EverfiUser user = mapper.treeToValue(emp, EverfiUser.class);
        return user;
    }

    private String generateJsonEntity() throws IOException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        // Create all Nodes.
        ArrayNode registrationsNode = mapper.createArrayNode();
        ObjectNode registrationsObj = registrationsNode.addObject();
        ObjectNode attributesNode = mapper.createObjectNode();
        ObjectNode dataNode = mapper.createObjectNode();
        ObjectNode rootNode = mapper.createObjectNode();

        if (categoryLabels != null && !categoryLabels.isEmpty()) {
            ArrayNode categoryLabelsNode = mapper.createArrayNode();
            for (EverfiCategoryLabel label : categoryLabels) {
                categoryLabelsNode.add(String.valueOf(label.getLabelId()));
            }
            registrationsObj.set("category_labels", categoryLabelsNode);
        }

        registrationsObj.put("rule_set", "user_rule_set");
        registrationsObj.put("first_name", firstName);
        registrationsObj.put("last_name", lastName);
        registrationsObj.put("email", email);
        if (ssoId != null) {
            registrationsObj.put("sso_id", ssoId);
        }
        registrationsObj.put("employee_id", employeeId);
        registrationsObj.put("active", isActive);

        attributesNode.set("registrations", registrationsNode);

        dataNode.put("type", "registration_sets");
        dataNode.put("id", empUuid);
        dataNode.set("attributes", attributesNode);

        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private String endpoint() {
        return String.format(UPDATE_USER_END_POINT, empUuid);
    }
}
