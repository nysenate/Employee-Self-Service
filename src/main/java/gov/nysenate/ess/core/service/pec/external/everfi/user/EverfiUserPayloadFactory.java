package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.util.OutputUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EverfiUserPayloadFactory {

    public String buildAddUserPayload(EverfiAddUserCommand command)
            throws JsonProcessingException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        ArrayNode categoryLabelsNode = mapper.createArrayNode();
        for (EverfiCategoryLabel label : command.categoryLabels()) {
            categoryLabelsNode.add(String.valueOf(label.getLabelId()));
        }

        ArrayNode registrationsNode = mapper.createArrayNode();
        ObjectNode registrationsObj = registrationsNode.addObject();
        registrationsObj.put("rule_set", "user_rule_set");
        registrationsObj.put("first_name", command.firstName());
        registrationsObj.put("last_name", command.lastName());
        registrationsObj.put("email", command.email());
        registrationsObj.put("employee_id", command.employeeId());
        registrationsObj.set("category_labels", categoryLabelsNode);

        ObjectNode registrationRoleObj = registrationsNode.addObject();
        registrationRoleObj.put("rule_set", "cc_learner");
        registrationRoleObj.put("role", "supervisor");

        return wrapRegistrationPayload("registration_sets", null, registrationsNode, mapper);
    }

    public String buildUpdateUserPayload(EverfiUpdateUserCommand command) throws JsonProcessingException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        ArrayNode registrationsNode = mapper.createArrayNode();
        ObjectNode registrationsObj = registrationsNode.addObject();
        registrationsObj.put("rule_set", "user_rule_set");
        registrationsObj.put("first_name", command.firstName());
        registrationsObj.put("last_name", command.lastName());
        registrationsObj.put("email", command.email());
        registrationsObj.put("employee_id", command.employeeId());
        registrationsObj.put("active", command.active());
        if (command.ssoId() != null) {
            registrationsObj.put("sso_id", command.ssoId());
        }
        if (command.categoryLabels() != null && !command.categoryLabels().isEmpty()) {
            ArrayNode categoryLabelsNode = mapper.createArrayNode();
            for (EverfiCategoryLabel label : command.categoryLabels()) {
                categoryLabelsNode.add(String.valueOf(label.getLabelId()));
            }
            registrationsObj.set("category_labels", categoryLabelsNode);
        }

        return wrapRegistrationPayload("registration_sets", command.uuid(), registrationsNode, mapper);
    }

    private String wrapRegistrationPayload(String type, String id, ArrayNode registrationsNode, ObjectMapper mapper)
            throws JsonProcessingException {
        ObjectNode attributesNode = mapper.createObjectNode();
        attributesNode.set("registrations", registrationsNode);

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("type", type);
        if (id != null) {
            dataNode.put("id", id);
        }
        dataNode.set("attributes", attributesNode);

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("data", dataNode);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
}
