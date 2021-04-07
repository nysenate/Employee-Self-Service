package gov.nysenate.ess.core.service.pec.external.everfi.user.add;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserService;
import gov.nysenate.ess.core.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class EverfiAddUserRequest {

    private static final String ADD_USER_END_POINT = "/v1/admin/registration_sets";
    private final EverfiApiClient everfiClient;

    private final int employeeId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<EverfiCategoryLabel> categoryLabels;

    private static final Logger logger = LoggerFactory.getLogger(EverfiAddUserRequest.class);

    public EverfiAddUserRequest(EverfiApiClient everfiClient, int employeeId, String firstName, String lastName,
                                String email, List<EverfiCategoryLabel> categoryLabels) {
        this.everfiClient = everfiClient;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.categoryLabels = categoryLabels;
    }

    /**
     * Adds a user represented by the fields in this class.
     * @return The added user returned from Everfi or null if an error occurred.
     * @throws IOException
     */
    public EverfiUser addUser() throws IOException {
        String entity = generateJsonEntity();
        if (this.email == null || this.email.isEmpty() ) {
            logger.warn("Could not add user to Everfi with EmpID " + employeeId + ". They do not have an email");
            return null;
        }
        
        String data = everfiClient.post(ADD_USER_END_POINT, entity);
        if (data == null) {
            return null;
        }

        ObjectMapper mapper = OutputUtils.jsonMapper;
        JsonNode rootNode = mapper.readTree(data);
        JsonNode emp = rootNode.get("data");
        EverfiUser user = mapper.treeToValue(emp, EverfiUser.class);
        return user;
    }

    private String generateJsonEntity() throws JsonProcessingException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        ArrayNode categoryLabelsNode = mapper.createArrayNode();
        for (EverfiCategoryLabel label : categoryLabels) {
            categoryLabelsNode.add(String.valueOf(label.getLabelId()));
        }

        ArrayNode registrationsNode = mapper.createArrayNode();
        ObjectNode registrationsObj = registrationsNode.addObject();
        registrationsObj.put("rule_set", "user_rule_set");
        registrationsObj.put("first_name", firstName);
        registrationsObj.put("last_name", lastName);
        registrationsObj.put("email", email);
        registrationsObj.put("employee_id", employeeId);
        registrationsObj.set("category_labels", categoryLabelsNode);

        ObjectNode registrationRoleObj = registrationsNode.addObject();
        registrationRoleObj.put("rule_set", "cc_learner");
        registrationRoleObj.put("role", "supervisor"); // Everyone is given the supervisor role.

        ObjectNode attributesNode = mapper.createObjectNode();
        attributesNode.set("registrations", registrationsNode);

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("type", "registration_sets");
        dataNode.set("attributes", attributesNode);

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

}
