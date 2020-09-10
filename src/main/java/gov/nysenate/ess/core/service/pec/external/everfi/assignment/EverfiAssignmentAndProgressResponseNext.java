package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiAssignmentAndProgressResponseNext {
    private String since;
    @JsonProperty("scroll_id")
    private String scrollId;
    @JsonProperty("scroll_size")
    private String scrollSize;
    //        private String filter;
    private String href;

    public EverfiAssignmentAndProgressResponseNext() {
    }

    public String getSince() {
        return since;
    }

    public String getScrollId() {
        return scrollId;
    }

    public String getScrollSize() {
        return scrollSize;
    }

    public String getHref() {
        return href;
    }

    @Override
    public String toString() {
        return "EverfiAssignmentNextResponse{" +
                "since='" + since + '\'' +
                ", scroll_id='" + scrollId + '\'' +
                ", scroll_size='" + scrollSize + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
