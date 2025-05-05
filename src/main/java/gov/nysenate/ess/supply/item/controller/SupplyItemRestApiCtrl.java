package gov.nysenate.ess.supply.item.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.base.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import gov.nysenate.ess.supply.item.ItemFilters;
import gov.nysenate.ess.supply.item.OrderableItems;
import gov.nysenate.ess.supply.item.SupplyItemService;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.item.view.SupplyItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.TreeSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/items")
public class SupplyItemRestApiCtrl extends BaseRestApiCtrl {
    private final SupplyItemService supplyItemService;
    private final SupplyItemDao supplyItemDao;
    private final LocationService locationService;

    @Autowired
    public SupplyItemRestApiCtrl(
            SupplyItemService supplyItemService,
            SupplyItemDao supplyItemDao,
            LocationService locationService) {
        this.supplyItemService = supplyItemService;
        this.supplyItemDao = supplyItemDao;
        this.locationService = locationService;
    }

    @RequestMapping("/{itemId}")
    public BaseResponse getSupplyItemById(@PathVariable int itemId) {
        return new ViewObjectResponse<>(new SupplyItemView(supplyItemDao.getItemById(itemId)));
    }

    /**
     * Supply Items API.
     * Returns all supply items.
     */
    @RequestMapping(value = "", params = "!locId")
    public BaseResponse getAllSupplyItems() {
        List<SupplyItem> items = supplyItemService.getAllItems();
        var itemViews = items.stream().map(SupplyItemView::new).collect(Collectors.toList());
        return ListViewResponse.of(itemViews);
    }

    /**
     * Supply Item Search API.
     * Returns supply items for the given locId which also match all provided filters.
     */
    @RequestMapping(value = "")
    public BaseResponse getSupplyItems(
            @RequestParam String locId,
            @RequestParam(defaultValue = "", required = false) String[] categories,
            @RequestParam(defaultValue = "", required = false) String term,
            @RequestParam(defaultValue = "name", required = false) String sort,
            WebRequest webRequest) {
        LocationId locationId = LocationId.ofString(locId);
        if (locationService.getLocation(locationId) == null) {
            throw new InvalidRequestParamEx(locId, "locId", "String", "locId must represent a valid location with the format: locCode-locType. e.g. A42FB-W");
        }
        Set<String> categoriesList = Set.of(categories);
        LimitOffset limitOffset = getLimitOffset(webRequest, 16);

        List<SupplyItem> items;
        if (getSubject().isPermitted(SupplyPermission.SUPPLY_EMPLOYEE.getPermission())) {
            // Supply employees can order any item regardless of location.
            items = supplyItemService.getAllItems();
        } else {
            items = supplyItemService.getItems(locationId);
        }
        PaginatedList<SupplyItem> paginatedItems = supplyItemService.filterItems(items, categoriesList, term, sort, limitOffset);
        var itemViews = paginatedItems.getResults().stream()
                .map(SupplyItemView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(itemViews, paginatedItems.getTotal(), paginatedItems.getLimOff());
    }

    /**
     * Supply Items Categories API
     * <p>
     * Returns a list of all distinct    item categories for the given locId and term sorted in natural order.
     *
     * @param locId
     * @param term
     * @return
     */
    @RequestMapping("/categories")
    public BaseResponse getCategories(@RequestParam String locId,
                                      @RequestParam(defaultValue = "", required = false) String term) {
        LocationId locationId = LocationId.ofString(locId);
        if (locationService.getLocation(locationId) == null) {
            throw new InvalidRequestParamEx(locId, "locId", "String",
                    "locId must represent a valid location with the format: locCode-locType. e.g. A42FB-W");
        }
        List<SupplyItem> items;
        if (getSubject().isPermitted(SupplyPermission.SUPPLY_EMPLOYEE.getPermission())) {
            // Supply employees can order any item regardless of location.
            items = supplyItemService.getAllItems();
        } else {
            items = supplyItemService.getItems(locationId);
        }
        items = ItemFilters.byTerm(items, term);
        Set<String> categories = items.stream()
                .map(i -> i.getCategory().getName())
                .collect(Collectors.toCollection(TreeSet::new));
        return ListViewResponse.ofStringList(categories, "categories", categories.size(), LimitOffset.ALL);
    }

    /**
     * Orderable Supply Items API.
     * <p>
     * Returns a List of items which are allowed to be ordered at a given location.
     * This removes hidden items and location restricted items from the response.
     * <p>
     * PathVariables: locId - A location id represented by a location code - location type. e.g. A42FB-W
     */
    @Deprecated
    @RequestMapping("/orderable/{locId}")
    public BaseResponse orderableSupplyItems(@PathVariable String locId) {
        LocationId locationId = LocationId.ofString(locId);
        if (locationService.getLocation(locationId) == null) {
            throw new InvalidRequestParamEx(locId, "locId", "String", "locId must represent a valid location with the format: locCode-locType. e.g. A42FB-W");
        }

        List<SupplyItem> items = supplyItemDao.getSupplyItems();
        if (getSubject().isPermitted(SupplyPermission.SUPPLY_EMPLOYEE.getPermission())) {
            // Supply staff are allowed to order all items at any location.
            return sortedItemViews(items);
        } else {
            return sortedItemViews(OrderableItems.forItemsAndLoc(items, locationId));
        }
    }

    private ListViewResponse<SupplyItemView> sortedItemViews(List<SupplyItem> items) {
        return ListViewResponse.of(items.stream()
                .map(SupplyItemView::new)
                .sorted()
                .collect(Collectors.toList()));
    }
}
