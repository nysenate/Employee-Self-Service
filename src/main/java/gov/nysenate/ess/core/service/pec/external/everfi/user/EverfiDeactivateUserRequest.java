package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;

/**
 * Activates or Deactivates Everfi Users.
 */
public class EverfiDeactivateUserRequest {

    private static final String DEACTIVATE_USER_END_POINT = "/v1/admin/registration_sets/:%s";
    private final EverfiApiClient everfiClient;

    private final String empUuid;
    private final int employeeId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final boolean active; // true to activate an emp, false to deactivate.

    public EverfiDeactivateUserRequest(EverfiApiClient everfiClient, String empUuid, int employeeId,
                                       String firstName, String lastName, String email, boolean active) {
        this.everfiClient = everfiClient;
        this.empUuid = empUuid;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    /**
     * Activates or deactivates an employee based on the {@code active} field.
     * @return The updated user from Everfi.
     * @throws IOException
     */
    public EverfiUser updateActiveStatus() throws IOException {
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

        ArrayNode registrationsNode = mapper.createArrayNode();
        ObjectNode registrationsObj = registrationsNode.addObject();
        registrationsObj.put("rule_set", "user_rule_set");
        registrationsObj.put("first_name", firstName);
        registrationsObj.put("last_name", lastName);
        registrationsObj.put("email", email);
        registrationsObj.put("employee_id", employeeId);
        registrationsObj.put("active", active);

        ObjectNode attributesNode = mapper.createObjectNode();
        attributesNode.set("registrations", registrationsNode);

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("type", "registration_sets");
        dataNode.put("id", empUuid);
        dataNode.set("attributes", attributesNode);

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    public String endpoint() {
        return String.format(DEACTIVATE_USER_END_POINT, empUuid);
    }
}
