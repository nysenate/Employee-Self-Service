package gov.nysenate.ess.supply.reconcilation.service;

import gov.nysenate.ess.supply.reconcilation.dao.RecOrderDao;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ReconciliationService {
    RecOrderDao recOrderDao = new RecOrderDao();

    public void reconcile(ArrayList<RecOrder> recOrders){
        ArrayList<RecOrder> oracle = new ArrayList<>(createOracleSet(recOrders));
        for(int i = 0; i < recOrders.size(); i++){
            System.out.println(recOrders.get(i).getItemId());
        }
        for(int i = 0; i < oracle.size(); i++){
            System.out.println(oracle.get(i).getItemId());
        }
    }



    private Set<RecOrder> createOracleSet(ArrayList<RecOrder> recOrders){
        HashSet<Integer> ids = createIdSet(recOrders);
        Set<RecOrder> oracle = recOrderDao.getItemsById(ids);
        return oracle;
    }

    private HashSet<Integer> createIdSet(ArrayList<RecOrder> recOrders){
        HashSet<Integer> ids =  new HashSet<>();
        for(RecOrder order : recOrders){
            ids.add(order.getItemId());
        }
        return ids;
    }

    private void compare(ArrayList<RecOrder> recOrders, ArrayList<RecOrder> oracle){


    }

}
