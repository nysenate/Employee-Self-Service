package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;

/**
 * A request to add a new category label in Everfi.
 *
 * This class's functionality should not be used directly. To add a label, use {@link EverfiCategoryService}.
 */
class EverfiAddCategoryLabelRequest {

    private static final String END_POINT = "/v1/admin/category_labels";
    private final EverfiApiClient client;
    private final int categoryId;
    private final String labelName;

    /**
     * @param client
     * @param categoryId The id of the category this label should belong to.
     * @param labelName The name this label should have.
     */
    EverfiAddCategoryLabelRequest(EverfiApiClient client, int categoryId, String labelName) {
        this.client = client;
        this.categoryId = categoryId;
        this.labelName = labelName;
    }

    /**
     * Adds a category label to a category in Everfi.
     * @return The newly added CategoryLabel.
     * @throws IOException
     */
    EverfiCategoryLabel addLabel() throws IOException {
        String data = client.post(END_POINT, generateJsonEntity());

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

        attributesNode.put("name", labelName);
        attributesNode.put("category_id", String.valueOf(categoryId));

        dataNode.put("type", "category_labels");
        dataNode.set("attributes", attributesNode);

        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
}

