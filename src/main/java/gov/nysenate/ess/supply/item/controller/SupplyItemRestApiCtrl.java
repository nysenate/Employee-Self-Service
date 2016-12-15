package gov.nysenate.ess.supply.item.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.item.OrderableItems;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.item.view.SupplyItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/items")
public class SupplyItemRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyItemRestApiCtrl.class);

    @Autowired
    private SupplyItemDao supplyItemDao;
    @Autowired
    private LocationService locationService;

    /**
     * Supply Items API.
     * <p>
     * Returns a list of all currently available supply items.
     */
    @RequestMapping("")
    public BaseResponse allSupplyItems() {
        Set<SupplyItem> items = supplyItemDao.getSupplyItems();
        return ListViewResponse.of(items.stream().map(SupplyItemView::new).collect(Collectors.toList()));
    }

    /**
     * Orderable Supply Items API.
     * <p>
     * Returns a List of items which are allowed to be ordered at a given location.
     * This removes hidden items and location restricted items from the response.
     */
    @RequestMapping("/{locId}")
    public BaseResponse orderableSupplyItems(@PathVariable String locId) {
//        LocationId locationId = LocationId(locId);
        Set<SupplyItem> items = supplyItemDao.getSupplyItems();
//        items = OrderableItems.forItemsAndLoc(items);
        return ListViewResponse.of(items.stream().map(SupplyItemView::new).collect(Collectors.toList()));
    }


}
