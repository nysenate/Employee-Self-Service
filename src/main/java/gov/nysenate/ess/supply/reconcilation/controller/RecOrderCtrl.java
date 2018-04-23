package gov.nysenate.ess.supply.reconcilation.controller;


import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import gov.nysenate.ess.supply.reconcilation.view.RecOrderView;
import gov.nysenate.ess.supply.requisition.controller.RequisitionRestApiCtrl;
import gov.nysenate.ess.supply.requisition.view.SubmitRequisitionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reconciliation/reconcile")
public class RecOrderCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionRestApiCtrl.class);

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitRecOrder(@RequestBody List<RecOrderView> recOrderViews){

        ArrayList<RecOrder> recOrders = new ArrayList<RecOrder>();

        for(RecOrderView order : recOrderViews){
            RecOrder recOrder = new RecOrder.Builder()
                    .withItemId(order.getItemId())
                    .withQuantity(order.getQuantity())
                    .build();
            recOrders.add(recOrder);
        }

        //return "you did something!";
        //BaseResponse
        return new ViewObjectResponse<>();

    }

}
