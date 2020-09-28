package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;

/**
 * Request to Update the name of a Everfi Category
 */
public class EverfiUpdateCategoryRequest {

    private static final String END_POINT = "/v1/admin/categories/%s";
    private final EverfiApiClient client;
    private final int categoryId;
    private final String newCategoryName;

    public EverfiUpdateCategoryRequest(EverfiApiClient client, int categoryId, String newCategoryName) {
        this.client = client;
        this.categoryId = categoryId;
        this.newCategoryName = newCategoryName;
    }

    public EverfiCategory updateCategory() throws IOException {
        String data = client.post(endpoint(), generateJsonEntity());

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

        attributesNode.put("name", newCategoryName);

        dataNode.put("type", "categories");
        dataNode.put("id", categoryId);
        dataNode.set("attributes", attributesNode);

        rootNode.set("data", dataNode);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }


    private String endpoint() {
        return String.format(END_POINT, categoryId);
    }
}
