package gov.nysenate.ess.core.model.pec.ethics;

import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDateTime;

public class DateRangedEthicsCode implements Comparable<DateRangedEthicsCode> {

    //Fields in this class are a 1 to 1 mapping of the ethics code table. It's ok for some fields to be unused
    private final int id;
    private final int task_id;
    private final int sequence_no;
    private final String label;
    private final String code;
    private final LocalDateTime start_date;
    private final LocalDateTime end_date;

    public DateRangedEthicsCode(int id, int task_id, int sequence_no, String label,
                                String code, LocalDateTime start_date, LocalDateTime end_date) {
        this.id = id;
        this.task_id = task_id;
        this.sequence_no = sequence_no;
        this.label = label;
        this.code = code;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public LocalDateTime getStartDate() {
        return start_date;
    }

    public LocalDateTime getEndDate() {
        return end_date;
    }

    public String getCode() {
        return code;
    }

    int getId() {
        return id;
    }

    public int getTaskId() {
        return task_id;
    }

    @Override
    public int compareTo(DateRangedEthicsCode o) {
        if (o.getId() < id) {
            return 1;
        }
        else {
            return -1;
        }
    }

}
