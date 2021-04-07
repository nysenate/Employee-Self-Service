package gov.nysenate.ess.core.service.pec.external.everfi.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiCategoryByIdResponse {

    @JsonProperty("included")
    private List<EverfiCategoryLabel> labels;

    public EverfiCategoryByIdResponse() {
    }

    public List<EverfiCategoryLabel> getLabels() {
        return labels;
    }
}
