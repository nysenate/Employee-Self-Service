package gov.nysenate.ess.travel.application.uncompleted;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StayDtoView implements ViewObject {

    String date;
    @JsonProperty(value="isMealsRequested")
    boolean isMealsRequested;
    @JsonProperty(value="isLodgingRequested")
    boolean isLodgingRequested;

    public StayDtoView() {
    }

    public String getDate() {
        return date;
    }

    public LocalDate getLocalDate() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    @JsonProperty(value="isMealsRequested")
    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    @JsonProperty(value="isLodgingRequested")
    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    @Override
    public String getViewType() {
        return "stay-dto";
    }
}
