package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class EverfiCategoryLabel {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int id;
    private String name;

    public EverfiCategoryLabel() {
    }

    public EverfiCategoryLabel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CategoryLabel{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
