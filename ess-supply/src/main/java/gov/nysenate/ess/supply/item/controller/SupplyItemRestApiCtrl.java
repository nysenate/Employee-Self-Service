package gov.nysenate.ess.supply.item.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.item.view.SupplyItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supplyItems")
public class SupplyItemRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyItemRestApiCtrl.class);

    @Autowired
    private SupplyItemService supplyItemService;

    @RequestMapping("")
    public BaseResponse getSupplyItems() {
        List<SupplyItem> items = supplyItemService.getSupplyItems();
        List<SupplyItemView> itemViews = items.stream().map(SupplyItemView::new).collect(Collectors.toList());
        return ListViewResponse.of(itemViews);
    }
}
