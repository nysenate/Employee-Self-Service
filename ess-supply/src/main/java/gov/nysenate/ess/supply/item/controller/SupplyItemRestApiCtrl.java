package gov.nysenate.ess.supply.item.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.item.view.SupplyItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/items")
public class SupplyItemRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyItemRestApiCtrl.class);

    @Autowired
    private SupplyItemService supplyItemService;

    /**
     * Supply Items API.
     * Request Parameters: limit - Limit the number of results.
     *                     offset - Start results from an offset.
     */
    @RequestMapping("")
    public BaseResponse getSupplyItems(WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 1000);
        PaginatedList<SupplyItem> paginatedItems = supplyItemService.getSupplyItems(limOff);
        List<SupplyItemView> itemViews = paginatedItems.getResults().stream().map(SupplyItemView::new).collect(Collectors.toList());
        return ListViewResponse.of(itemViews, paginatedItems.getTotal(), paginatedItems.getLimOff());
    }
}
