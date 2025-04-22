package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reports")
public class ItemSummaryCtrl extends BaseRestApiCtrl {

    @Autowired private ItemSummaryService itemSummaryService;

    @RequestMapping("/item-summary")
    public BaseResponse itemSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDateTime,
            @RequestParam(defaultValue = "") String locationCode,
            @RequestParam(defaultValue = "") String commodityCode) {
        checkPermission(SupplyPermission.SUPPLY_UI_MANAGE_ITEM_HISTORY.getPermission());
        List<ItemSummary> summaries = itemSummaryService.createItemRequisitionSummaries(
                fromDateTime,
                toDateTime,
                locationCode,
                commodityCode);
        return ListViewResponse.of(summaries.stream()
                .map(ItemSummaryView::new)
                .collect(Collectors.toList()));
    }
}
