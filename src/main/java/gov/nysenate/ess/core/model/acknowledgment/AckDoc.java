package gov.nysenate.ess.core.model.acknowledgment;

import java.time.LocalDateTime;

public class AckDoc {

    private String title;
    private String filename;
    private Boolean active;
    private Integer id;
    private LocalDateTime effectiveDateTime;

    public AckDoc() {}

    public AckDoc(String title, String filename, Boolean active, Integer id, LocalDateTime effectiveDateTime) {
        this.title = title;
        this.filename = filename;
        this.active = active;
        this.id = id;
        this.effectiveDateTime = effectiveDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(LocalDateTime effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }
}
