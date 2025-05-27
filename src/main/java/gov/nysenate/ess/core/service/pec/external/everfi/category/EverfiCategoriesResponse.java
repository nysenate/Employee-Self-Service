package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiCategoriesResponse {

    @JsonProperty("data")
    private List<EverfiCategory> categories;

    public EverfiCategoriesResponse() {
    }

    public List<EverfiCategory> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return "EverfiCategoriesResponse{" +
                "categories=" + categories +
                '}';
    }
}
