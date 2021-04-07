package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;

/**
 * Request to add a new Category in Everfi.
 *
 * This class's functionality should not be used directly. To add a category, use {@link EverfiCategoryService}.
 */
class EverfiAddCategoryRequest {

    private static final String END_POINT = "/v1/admin/categories";
    private final EverfiApiClient client;
    private final String categoryName;

    EverfiAddCategoryRequest(EverfiApiClient client, String categoryName) {
        this.client = client;
        this.categoryName = categoryName;
    }

    EverfiCategory addCategory() throws IOException {
        String data = client.post(END_POINT, generateJsonEntity());
        if (data == null) {
            return null;
        }

        ObjectMapper mapper = OutputUtils.jsonMapper;
        JsonNode rootNode = mapper.readTree(data);
        JsonNode categoryNode = rootNode.get("data");
        return mapper.treeToValue(categoryNode, EverfiCategory.class);
    }

    private String generateJsonEntity() throws JsonProcessingException {
        ObjectMapper mapper = OutputUtils.jsonMapper;

        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode dataNode = mapper.createObjectNode();
        ObjectNode attributesNode = mapper.createObjectNode();

        attributesNode.put("name", categoryName);

        dataNode.put("type", "categories");
        dataNode.set("attributes", attributesNode);

        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
}
