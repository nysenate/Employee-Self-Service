package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Stateless gateway for the small set of remote category operations the application performs:
 * fetching the current categories as an {@link EverfiCategorySnapshot}, and creating new
 * categories or labels.
 * <p>
 * All read access lives on {@link EverfiCategorySnapshot}. Callers are expected to fetch a
 * snapshot at the start of a process and pass it through any code that needs lookups.
 */
@Service
public class EverfiCategoryService {

    private final EverfiApiClient client;

    @Autowired
    public EverfiCategoryService(EverfiApiClient client) {
        this.client = client;
    }

    /**
     * Fetch the full set of categories from Everfi and return them as an immutable snapshot.
     */
    public EverfiCategorySnapshot fetchSnapshot() throws IOException {
        return new EverfiCategorySnapshot(new EverfiGetCategoriesRequest(client).fetch());
    }

    /**
     * Add an {@link EverfiCategory} to Everfi. Does not verify that a category with the same
     * name already exists — callers should check a snapshot first if they need to avoid duplicates.
     */
    public EverfiCategory createCategory(String categoryName) throws IOException {
        return new EverfiAddCategoryRequest(client, categoryName).addCategory();
    }

    /**
     * Add an {@link EverfiCategoryLabel} to the given category. Does not verify that a label
     * with the same name already exists — callers should check a snapshot first if they need
     * to avoid duplicates.
     */
    public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
        return new EverfiAddCategoryLabelRequest(client, category.getId(), labelName).addLabel();
    }
}
