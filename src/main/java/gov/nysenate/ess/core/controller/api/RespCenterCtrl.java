package gov.nysenate.ess.core.controller.api;


import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.RespCenterHeadView;
import gov.nysenate.ess.core.dao.personnel.rch.ResponsibilityHeadDao;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/respctr")
public class RespCenterCtrl extends BaseRestApiCtrl {

    private final ResponsibilityHeadDao respHeadDao;

    public RespCenterCtrl(ResponsibilityHeadDao respHeadDao) {
        this.respHeadDao = respHeadDao;
    }

    @RequestMapping(value = "/head/search", method = {GET, HEAD})
    public ListViewResponse<RespCenterHeadView> searchRCH(@RequestParam(defaultValue = "") String term,
                                                          @RequestParam(defaultValue = "ASC") String sortOrder,
                                                          WebRequest request) {
        SortOrder parsedSortOrder = getEnumParameter("sortOrder", sortOrder, SortOrder.class);
        LimitOffset limitOffset = getLimitOffset(request, 10);
        PaginatedList<ResponsibilityHead> results = respHeadDao.rchSearch(term, limitOffset, parsedSortOrder);
        return ListViewResponse.fromPaginatedList(results, RespCenterHeadView::new);
    }
}
