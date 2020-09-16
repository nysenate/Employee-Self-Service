package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.List;

public class EverfiCategoriesRequest {

    private static final String CATEGORIES_ENDPOINT = "/v1/admin/categories/?include=category_labels";
    private static final String CATEGORY_BY_ID_ENDPOINT = "/v1/admin/categories/%s?include=category_labels";
    private final EverfiApiClient everfiClient;
    private EverfiCategoriesResponse response;

    public EverfiCategoriesRequest(EverfiApiClient everfiClient) {
        this.everfiClient = everfiClient;
    }

    public List<EverfiCategory> fetch() throws IOException {
        String data = everfiClient.get(CATEGORIES_ENDPOINT);
        response = OutputUtils.jsonToObject(data, EverfiCategoriesResponse.class);
        List<EverfiCategory> categories = response.getCategories();

        for (EverfiCategory category : categories) {
            String categoryEndpoint = String.format(CATEGORY_BY_ID_ENDPOINT, category.getId());
            data = everfiClient.get(categoryEndpoint);
            EverfiCategoryByIdResponse res = OutputUtils.jsonToObject(data, EverfiCategoryByIdResponse.class);
            category.setLabels(res.getLabels());
        }

        return categories;
    }
}
