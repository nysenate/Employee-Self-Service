package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;

import java.time.ZonedDateTime;
import java.util.List;

public class EverfiUser {

   @JsonProperty("id")
    private String uuid; // Everfi's uuid associated with this user.
    private EverfiUserAttributes attributes;
    @JsonProperty("category_labels")
    private List<EverfiCategoryLabel> categoryLabels;


    public EverfiUser() {
    }

    public String getUuid() {
        return uuid;
    }

    public EverfiUserAttributes getAttributes() {
        return attributes;
    }

    public List<EverfiCategoryLabel> getCategoryLabels() {
        return categoryLabels;
    }

    @JsonIgnore
    public int getEmployeeId() {
        return getAttributes().getEmployeeId();
    }

    @JsonIgnore
    public boolean isActive() {
        return getAttributes().isActive();
    }

    @JsonIgnore
    public ZonedDateTime getCreatedAt() {
        return getAttributes().getCreatedAt();
    }

    @JsonIgnore
    public String getEmail() {
        return getAttributes().getEmail();
    }

    @JsonIgnore
    public String getFirstName() {
        return getAttributes().getFirstName();
    }

    @JsonIgnore
    public String getLastName() {
        return getAttributes().getLastName();
    }


    @Override
    public String toString() {
        return "EverfiUser{" +
                "uuid='" + uuid + '\'' +
                ", attributes=" + attributes +
                ", categoryLabels=" + categoryLabels +
                '}';
    }
}
