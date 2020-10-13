package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EverfiCategoryService {

    private static final String UPLOAD_LIST = "Upload List"; // Upload List category name
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private final EverfiApiClient client;
    // Im memory storage of categories.
    private List<EverfiCategory> categories;

    @Autowired
    public EverfiCategoryService(EverfiApiClient client) {
        this.client = client;
    }

    /**
     * Add a EverfiCategory to Everfi
     *
     * @param categoryName The name of the new category.
     * @return The created EverfiCategory.
     * @throws IOException              if there is an error when updating Everfi.
     * @throws IllegalArgumentException if a category with name = {@code categoryName} already exists in Everfi.
     */
    public synchronized EverfiCategory createCategory(String categoryName) throws IOException {
        if (getCategory(categoryName) != null) {
            throw new IllegalArgumentException("Category with name \"" + categoryName + "\" already exists in Everfi. " +
                    "Cannot create another category with the same name.");
        }
        EverfiAddCategoryRequest addCategoryRequest = new EverfiAddCategoryRequest(client, categoryName);
        EverfiCategory category = addCategoryRequest.addCategory();
        loadCategories();
        return category;
    }

    /**
     * Add an EverfiCategoryLabel to an EverfiCategory.
     *
     * @param category  The category this label should belong to.
     * @param labelName The name of the new label.
     * @return The newly created CategoryLabel.
     * @throws IOException              if there is an error when adding this label to Everfi.
     * @throws IllegalArgumentException if a label with name = {@code labelName} already belongs to {@code category}
     */
    public synchronized EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
        if (getCategoryLabel(category.getName(), labelName) != null) {
            throw new IllegalArgumentException("Label with name \"" + labelName + "\" belonging to category \""
                    + category.getName() + "\" already exists in Everfi. Cannot create another label with the same name.");
        }
        EverfiAddCategoryLabelRequest addLabelRequest =
                new EverfiAddCategoryLabelRequest(client, category.getId(), labelName);
        EverfiCategoryLabel label = addLabelRequest.addLabel();
        loadCategories();
        return label;
    }

    /**
     * Creates a new department label with the given name.
     * @param name
     * @return
     */
    public EverfiCategoryLabel createDepartmentLabel(String name) throws IOException {
        EverfiCategory category = getCategory("Department");
        return createLabel(category, name);
    }

    /**
     * Creates a new Upload List label for the given date.
     *
     * @param date
     * @return The created CategoryLabel
     */
    public EverfiCategoryLabel createUploadListLabel(LocalDate date) throws IOException {
        EverfiCategory category = getCategory(UPLOAD_LIST);
        return createLabel(category, date.format(FORMAT));
    }

    /**
     * Get an EverfiUsers "Upload List" category label.
     *
     * @param user
     * @return The users "Upload List" category label or null if they don't have one.
     */
    public EverfiCategoryLabel getUserUploadListLabel(EverfiUser user) {
        for (EverfiCategoryLabel label : user.getCategoryLabels()) {
            if (label.getCategoryName().equals(UPLOAD_LIST)) {
                return label;
            }
        }
        return null;
    }

    /**
     * Get the "Upload List" label for the given date.
     *
     * @param date
     * @return The matching Upload List label or null if it does not exist.
     */
    public EverfiCategoryLabel getUploadListLabel(LocalDate date) throws IOException {
        return getCategoryLabel(UPLOAD_LIST, date.format(FORMAT));
    }

    /**
     * Get the given employee's CategoryLabel for the "Attended Live" Category.
     * <p>
     * Currently, this is always "No"
     *
     * @param emp
     * @return The appropriate label for the employee or null if the appropriate label does not exist.
     */
    public EverfiCategoryLabel getAttendLiveLabel(Employee emp) throws IOException {
        return getCategoryLabel("Attended Live", "No");
    }

    /**
     * Get the given employee's CategoryLabel for the "Department" Category.
     *
     * @param emp
     * @return The appropriate label for the employee or null if the appropriate label does not exist.
     */
    public EverfiCategoryLabel getDepartmentLabel(Employee emp) throws IOException {
        return getCategoryLabel("Department", emp.getRespCenterHeadCode());
    }

    /**
     * Get the given employee's CategoryLabel for the "Role" Category.
     *
     * @param emp
     * @return The appropriate label for the employee or null if the appropriate label does not exist.
     */
    public EverfiCategoryLabel getRoleLabel(Employee emp) throws IOException {
        return emp.isSenator()
                ? getCategoryLabel("Role", "Senator")
                : getCategoryLabel("Role", "Employee");
    }

    private EverfiCategoryLabel getCategoryLabel(String categoryName, String labelName) throws IOException {
        EverfiCategory cat = getCategory(categoryName);
        return cat == null ? null : getCategoryLabel(cat, labelName);
    }

    private EverfiCategory getCategory(String name) throws IOException {
        for (EverfiCategory category : getCategories()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    private EverfiCategoryLabel getCategoryLabel(EverfiCategory category, String labelName) {
        for (EverfiCategoryLabel label : category.getLabels()) {
            if (label.getLabelName().equals(labelName)) {
                return label;
            }
        }
        return null;
    }

    private List<EverfiCategory> getCategories() throws IOException {
        if (CollectionUtils.isEmpty(this.categories)) {
            loadCategories();
        }
        return this.categories;
    }

    private void loadCategories() throws IOException {
        EverfiGetCategoriesRequest getCategoriesRequest = new EverfiGetCategoriesRequest(client);
        this.categories = getCategoriesRequest.fetch();
    }
}
