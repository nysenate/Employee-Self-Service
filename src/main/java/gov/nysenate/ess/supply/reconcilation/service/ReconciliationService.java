package gov.nysenate.ess.supply.reconcilation.service;

import gov.nysenate.ess.supply.reconcilation.dao.RecOrderDao;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReconciliationService {

    @Autowired
    RecOrderDao recOrderDao;

    public void reconcile(ArrayList<RecOrder> recOrders){
        List<RecOrder> oracle = new ArrayList<RecOrder>(createOracleSet(recOrders));


    }

    private HashSet<RecOrder> createOracleSet(ArrayList<RecOrder> recOrders){
        //String ids = createIds(recOrders);
        HashSet ids = createIdSet(recOrders);
        HashSet<RecOrder> oracle = recOrderDao.getItemsById(ids);
        //Set<RecOrder> oracle = recOrderDao.getItems();
        return oracle;
    }

    private String createIds(ArrayList<RecOrder> recOrders){
        ArrayList<String> idList = new ArrayList<>();
        for(RecOrder order: recOrders){
            idList.add("'" + String.valueOf(order.getItemId()) + "'");
        }
        String ids = String.join(",", idList);
        return ids;
    }

    private HashSet<Integer> createIdSet(ArrayList<RecOrder> recOrders){
        HashSet<Integer> ids = new HashSet();
        for(RecOrder order: recOrders){
            ids.add(order.getItemId());
        }
        return ids;
    }

    private Set<RecOrder> createAllOracleSet(){
        Set<RecOrder> oracle = recOrderDao.getItems();
        return oracle;
    }
    
}
