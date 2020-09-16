package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class EverfiCategory {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int id;
    private String name;
    private List<EverfiCategoryLabel> labels;

    public EverfiCategory() {
    }

    public EverfiCategory(int id, String name, List<EverfiCategoryLabel> labels) {
        this.id = id;
        this.name = name;
        this.labels = labels;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<EverfiCategoryLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<EverfiCategoryLabel> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "EverfiCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", labels=" + labels +
                '}';
    }

    /**
     * Manually extract 'attributes.name' from the json to the name field.
     * @param attributes
     */
    @JsonProperty("attributes")
    private void unpackAttributes(Map<String, Object> attributes) {
        this.name = (String) attributes.get("name");
    }
}
