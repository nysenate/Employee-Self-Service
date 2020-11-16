package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class EverfiCategoryLabel {

    private int categoryId; // The id of the category this label belongs to.
    private String categoryName; // The name of the category this label belongs to.
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int labelId; // The id of this label.
    private String labelName; // The name of this label.

    public EverfiCategoryLabel() {
    }

    public EverfiCategoryLabel(int labelId, String labelName) {
        this.labelId = labelId;
        this.labelName = labelName;
    }

    public int getLabelId() {
        return labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return "EverfiCategoryLabel{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", id=" + labelId +
                ", name='" + labelName + '\'' +
                '}';
    }

    /**
     * Manually extract 'attributes.name' from the json to the name field.
     * @param attributes
     */
    @JsonProperty("attributes")
    private void unpackAttributes(Map<String, Object> attributes) {
        this.labelName = (String) attributes.get("name");
        this.categoryId = (Integer) attributes.get("category_id");
        this.categoryName = (String) attributes.get("category_name");
    }

    @JsonProperty("id")
    private void upackLabelId(String labelId) {
        this.labelId = Integer.parseInt(labelId);
    }
}
