package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.Category;

public class CategoryView implements ViewObject {

    protected String name;

    public CategoryView() {}

    public CategoryView(Category category) {
        this.name = category.getName();
    }

    public Category toCategory() {
        return new Category(this.name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getViewType() {
        return "category-view";
    }
}
