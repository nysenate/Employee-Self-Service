package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-owned in-memory cache of Everfi categories plus the remote write operations used by ESS.
 */
@Service
public class EverfiCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(EverfiCategoryService.class);

    private final EverfiApiClient client;
    private final boolean refreshEnabled;
    private volatile CacheState cacheState = CacheState.empty();

    @Autowired
    public EverfiCategoryService(
            EverfiApiClient client,
            @Value("${everfi.category.cache.refresh.enabled:false}") boolean refreshEnabled
    ) {
        this.client = client;
        this.refreshEnabled = refreshEnabled;
    }

    public EverfiCategoryService(EverfiApiClient client) {
        this(client, false);
    }

    @PostConstruct
    public void initializeOnStartup() {
        if (!refreshEnabled) {
            return;
        }
        try {
            initialize();
        } catch (IOException | RuntimeException ex) {
            logger.error("Failed to initialize Everfi category cache during startup. Scheduled refresh may recover.", ex);
        }
    }

    @Scheduled(cron = "${everfi.category.cache.refresh.cron:0 0 * * * *}")
    public void refreshScheduledCache() throws IOException {
        if (refreshEnabled) {
            initialize();
        }
    }

    public void initialize() throws IOException {
        initialize(new EverfiGetCategoriesRequest(client).fetch());
    }

    public void initialize(List<EverfiCategory> categories) {
        this.cacheState = CacheState.from(categories);
    }

    public List<EverfiCategory> getCategories() {
        return cacheState.categories();
    }

    public EverfiCategory getCategory(String name) {
        return cacheState.categoriesByName().get(name);
    }

    public EverfiCategoryLabel getCategoryLabel(EverfiCategory category, String labelName) {
        if (category == null) {
            return null;
        }
        Map<String, EverfiCategoryLabel> labels = cacheState.labelsByCategoryName().get(category.getName());
        return labels == null ? null : labels.get(labelName);
    }

    public EverfiCategoryLabel getCategoryLabel(String categoryName, String labelName) {
        Map<String, EverfiCategoryLabel> labels = cacheState.labelsByCategoryName().get(categoryName);
        return labels == null ? null : labels.get(labelName);
    }

    public EverfiCategoryLabel findLabel(int labelId) {
        return cacheState.labelsById().get(labelId);
    }

    /**
     * Replaces sparse label references with fully populated labels from the current cache.
     * Unknown label ids are dropped.
     */
    public List<EverfiCategoryLabel> hydrateLabels(List<EverfiCategoryLabel> sparseLabels) {
        if (sparseLabels == null) {
            return null;
        }
        List<EverfiCategoryLabel> hydrated = new ArrayList<>();
        for (EverfiCategoryLabel sparseLabel : sparseLabels) {
            EverfiCategoryLabel cachedLabel = findLabel(sparseLabel.getLabelId());
            if (cachedLabel != null) {
                sparseLabel.setCategoryId(cachedLabel.getCategoryId());
                sparseLabel.setCategoryName(cachedLabel.getCategoryName());
                sparseLabel.setLabelName(cachedLabel.getLabelName());
                hydrated.add(sparseLabel);
            }
        }
        return hydrated.isEmpty() ? null : hydrated;
    }

    /**
     * Add an {@link EverfiCategory} to Everfi. Does not verify that a category with the same
     * name already exists — callers should check the cache first if they need to avoid duplicates.
     */
    public EverfiCategory createCategory(String categoryName) throws IOException {
        EverfiCategory created = new EverfiAddCategoryRequest(client, categoryName).addCategory();
        refreshIfCreated(created != null ? created.getId() > 0 : false);
        return created;
    }

    /**
     * Add an {@link EverfiCategoryLabel} to the given category. Does not verify that a label
     * with the same name already exists — callers should check the cache first if they need
     * to avoid duplicates.
     */
    public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
        EverfiCategoryLabel created = new EverfiAddCategoryLabelRequest(client, category.getId(), labelName).addLabel();
        refreshIfCreated(created != null ? created.getLabelId() > 0 : false);
        return created;
    }

    /**
     * Create the Upload List label for the given date in the given Upload List category.
     */
    public EverfiCategoryLabel createUploadListLabel(EverfiCategory uploadListCategory, LocalDate date)
            throws IOException {
        return createLabel(uploadListCategory, EverfiCategoryRules.uploadListLabelName(date));
    }

    private void refreshIfCreated(boolean created) throws IOException {
        if (created) {
            initialize();
        }
    }

    private record CacheState(
            List<EverfiCategory> categories,
            Map<String, EverfiCategory> categoriesByName,
            Map<String, Map<String, EverfiCategoryLabel>> labelsByCategoryName,
            Map<Integer, EverfiCategoryLabel> labelsById
    ) {
        private static CacheState empty() {
            return new CacheState(List.of(), Map.of(), Map.of(), Map.of());
        }

        private static CacheState from(List<EverfiCategory> sourceCategories) {
            List<EverfiCategory> categories = Collections.unmodifiableList(new ArrayList<>(sourceCategories));
            Map<String, EverfiCategory> categoriesByName = new LinkedHashMap<>();
            Map<String, Map<String, EverfiCategoryLabel>> labelsByCategoryName = new LinkedHashMap<>();
            Map<Integer, EverfiCategoryLabel> labelsById = new LinkedHashMap<>();

            for (EverfiCategory category : categories) {
                categoriesByName.put(category.getName(), category);
                Map<String, EverfiCategoryLabel> labelsByName = new LinkedHashMap<>();
                for (EverfiCategoryLabel label : category.getLabels()) {
                    labelsByName.put(label.getLabelName(), label);
                    labelsById.put(label.getLabelId(), label);
                }
                labelsByCategoryName.put(category.getName(), Collections.unmodifiableMap(labelsByName));
            }
            return new CacheState(
                    categories,
                    Collections.unmodifiableMap(categoriesByName),
                    Collections.unmodifiableMap(labelsByCategoryName),
                    Collections.unmodifiableMap(labelsById)
            );
        }
    }
}
