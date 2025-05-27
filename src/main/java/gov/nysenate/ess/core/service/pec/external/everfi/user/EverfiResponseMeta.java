package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiResponseMeta {

    @JsonProperty("total_count")
    public int totalCount;
    @JsonProperty("cursor_id")
    public int cursorId;

    public EverfiResponseMeta() {
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCursorId() {
        return cursorId;
    }

    @Override
    public String toString() {
        return "EverfiResponseMeta{" +
                "totalCount=" + totalCount +
                ", cursorId=" + cursorId +
                '}';
    }
}
