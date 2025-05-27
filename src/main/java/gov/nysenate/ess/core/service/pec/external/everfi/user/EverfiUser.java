package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EverfiUser {

    private List<EverfiCategoryLabel> userCategoryLabels;

   @JsonProperty("id")
    private String uuid; // Everfi's uuid associated with this user.
    private EverfiUserAttributes attributes;

    @SuppressWarnings("unchecked")
    @JsonProperty("relationships")
    private void unpackNested(Map<String,Object> relationships) {
        Map<String,Object> catgeoryLabelsJson = (Map<String,Object>) relationships.get("category_labels");
        List<LinkedHashMap<String,String>> data = (List<LinkedHashMap<String,String>>) catgeoryLabelsJson.get("data");
        ArrayList<EverfiCategoryLabel> everfiCategoryLabels = new ArrayList<>();
        for (int i=0; i < data.size(); i++) {
            LinkedHashMap<String,String> turnIntoLabel = (LinkedHashMap<String,String>) data.get(i);
            everfiCategoryLabels.add(
                    new EverfiCategoryLabel( Integer.parseInt(turnIntoLabel.get("id")) , turnIntoLabel.get("type") ) );
        }
        this.userCategoryLabels = everfiCategoryLabels;
    }

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

    @JsonIgnore
    public List<EverfiCategoryLabel> getUserCategoryLabels() {
        return this.userCategoryLabels;
    }

    public void setUserCategoryLabels(List<EverfiCategoryLabel> newUserCategoryLabels) {
        this.userCategoryLabels = newUserCategoryLabels;
    }


    @Override
    public String toString() {
        return "EverfiUser{" +
                "uuid='" + uuid + '\'' +
                ", attributes=" + attributes +
                ", categoryLabels=" + userCategoryLabels +
                '}';
    }
}
