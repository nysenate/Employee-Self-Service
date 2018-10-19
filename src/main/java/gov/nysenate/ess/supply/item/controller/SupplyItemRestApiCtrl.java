package gov.nysenate.ess.supply.item.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
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

    private SupplyItemDao supplyItemDao;
    private LocationService locationService;

    @Autowired
    public SupplyItemRestApiCtrl(SupplyItemDao supplyItemDao, LocationService locationService) {
        this.supplyItemDao = supplyItemDao;
        this.locationService = locationService;
    }

    @RequestMapping("/{itemId}")
    public BaseResponse getSupplyItemById(@PathVariable int itemId) {
        return new ViewObjectResponse<>(new SupplyItemView(supplyItemDao.getItemById(itemId)));
    }

    /**
     * Supply Items API.
     * <p>
     * Returns a list of all currently available supply items.
     */
    @RequestMapping("")
    public BaseResponse allSupplyItems() {
        Set<SupplyItem> items = supplyItemDao.getSupplyItems();
        return sortedItemViews(items);
    }

    /**
     * Orderable Supply Items API.
     * <p>
     * Returns a List of items which are allowed to be ordered at a given location.
     * This removes hidden items and location restricted items from the response.
     * <p>
     * PathVariables: locId - A location id represented by a location code - location type. e.g. A42FB-W
     */
    @RequestMapping("/orderable/{locId}")
    public BaseResponse orderableSupplyItems(@PathVariable String locId) {
        LocationId locationId = LocationId.ofString(locId);
        if (locationService.getLocation(locationId) == null) {
            throw new InvalidRequestParamEx(locId, "locId", "String", "locId must represent a valid location with the format: locCode-locType. e.g. A42FB-W");
        }
        Set<SupplyItem> items = supplyItemDao.getSupplyItems();
        return sortedItemViews(OrderableItems.forItemsAndLoc(items, locationId));
    }

    private ListViewResponse<SupplyItemView> sortedItemViews(Set<SupplyItem> items) {
        return ListViewResponse.of(items.stream()
                .map(SupplyItemView::new)
                .sorted()
                .collect(Collectors.toList()));
    }
}
