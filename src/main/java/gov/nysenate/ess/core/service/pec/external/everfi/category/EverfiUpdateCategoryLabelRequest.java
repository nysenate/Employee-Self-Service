package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;

/**
 * Request to update the name of an Everfi Category Label.
 */
public class EverfiUpdateCategoryLabelRequest {

    private static final String END_POINT = "/v1/admin/category_labels/%s";
    private final EverfiApiClient client;
    private final int labelId;
    private final String newLabelName;

    public EverfiUpdateCategoryLabelRequest(EverfiApiClient client, int labelId, String newLabelName) {
        this.client = client;
        this.labelId = labelId;
        this.newLabelName = newLabelName;
    }

    /**
     * Updates a Everfi Category Label.
     * @return The updated label or null if an error occurred.
     * @throws IOException
     */
    public EverfiCategoryLabel updateLabel() throws IOException {
        String data = client.patch(endpoint(), generateJsonEntity());
        if (data == null) {
            return null;
        }

        ObjectMapper mapper = OutputUtils.jsonMapper;
        JsonNode rootNode = mapper.readTree(data);
        JsonNode labelNode = rootNode.get("data");
        return mapper.treeToValue(labelNode, EverfiCategoryLabel.class);
    }

    private String generateJsonEntity() throws JsonProcessingException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode dataNode = mapper.createObjectNode();
        ObjectNode attributesNode = mapper.createObjectNode();

        attributesNode.put("name", newLabelName);

        dataNode.put("id", labelId);
        dataNode.put("type", "category_labels");
        dataNode.set("attributes", attributesNode);

        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private String endpoint() {
        return String.format(END_POINT, labelId);
    }
}
