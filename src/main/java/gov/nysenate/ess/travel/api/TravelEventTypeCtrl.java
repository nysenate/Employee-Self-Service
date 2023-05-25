package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.api.application.EventTypeView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/event-types")
public class TravelEventTypeCtrl extends BaseRestApiCtrl {

    @RequestMapping("")
    public BaseResponse fetchTravelEventTypes() {
        List<EventTypeView> eventTypeViews = EnumSet.allOf(EventType.class)
                .stream()
                .sorted(Comparator.comparing(EventType::ordinal))
                .map(EventTypeView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(eventTypeViews);
    }
}
