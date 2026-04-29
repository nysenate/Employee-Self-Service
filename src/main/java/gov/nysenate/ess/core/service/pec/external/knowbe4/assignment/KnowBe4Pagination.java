package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KnowBe4Pagination {

    @JsonProperty("per_page")
    private Integer perPage;

    @JsonProperty("next_cursor")
    private String nextCursor;

    public KnowBe4Pagination() {}

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
