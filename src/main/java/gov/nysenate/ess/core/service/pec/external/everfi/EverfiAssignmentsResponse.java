package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiAssignmentsResponse {

    private EverfiAssignmentNextResponse next;

    @JsonProperty("data")
    private List<EverfiAssignment> assignments;

    public EverfiAssignmentsResponse() {
    }

    public EverfiAssignmentNextResponse getNext() {
        return next;
    }

    public List<EverfiAssignment> getAssignments() {
        return assignments;
    }

    @Override
    public String toString() {
        return "EverfiAssignmentsResponse{" +
                "next=" + next +
                ", assignments=" + assignments +
                '}';
    }

    public class EverfiAssignmentNextResponse {
        public String since;
        public String scroll_id;
        public String scroll_size;
//        public String filter;
        public String href;

        public EverfiAssignmentNextResponse() {
        }

        @Override
        public String toString() {
            return "EverfiAssignmentNextResponse{" +
                    "since='" + since + '\'' +
                    ", scroll_id='" + scroll_id + '\'' +
                    ", scroll_size='" + scroll_size + '\'' +
                    ", href='" + href + '\'' +
                    '}';
        }
    }
}
