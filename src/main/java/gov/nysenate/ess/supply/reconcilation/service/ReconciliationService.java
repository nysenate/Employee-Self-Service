package gov.nysenate.ess.supply.reconcilation.service;

import gov.nysenate.ess.supply.reconcilation.dao.RecOrderDao;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class ReconciliationService {

    @Autowired
    RecOrderDao recOrderDao;

    public void reconcile(ArrayList<RecOrder> recOrders){
        ArrayList<RecOrder> oracle = new ArrayList<>(createOracleSet(recOrders));
        Collections.reverse(oracle);

        //debugging
        for(int i = 0; i < recOrders.size(); i++){
            System.out.println("Rec Id: " + recOrders.get(i).getItemId() + " Rec Q: " + recOrders.get(i).getQuantity());
            System.out.println("O Id: " + oracle.get(i).getItemId() + " O Q: " + oracle.get(i).getQuantity());
        }

        ArrayList<RecOrder> sameQuantityOrders = getSameOrders(recOrders, oracle);
        ArrayList<RecOrder> differentQuantityOrders = getDifferentOrders(recOrders, oracle);

        //debugging
        for(RecOrder order: sameQuantityOrders){
            System.out.println(order.getItemId());
        }
        for(RecOrder order: differentQuantityOrders){
            System.out.println(order.getItemId());
        }

    }

    private HashSet<RecOrder> createOracleSet(ArrayList<RecOrder> recOrders){
        HashSet ids = createIdSet(recOrders);
        HashSet<RecOrder> oracle = recOrderDao.getItemsById(ids);
        return oracle;
    }


    private HashSet<Integer> createIdSet(ArrayList<RecOrder> recOrders){
        HashSet<Integer> ids = new HashSet();
        for(RecOrder order: recOrders){
            ids.add(order.getItemId());
        }
        return ids;
    }

    private ArrayList<RecOrder> getSameOrders(ArrayList<RecOrder> recOrders, ArrayList<RecOrder> oracle){
        ArrayList<RecOrder> sameQuantityOrders = new ArrayList<>();
        for(int i = 0; i < recOrders.size(); i++){
            if(recOrders.get(i).getQuantity() == oracle.get(i).getQuantity()){
                sameQuantityOrders.add(recOrders.get(i));
            }
        }
        return sameQuantityOrders;
    }

    private ArrayList<RecOrder> getDifferentOrders(ArrayList<RecOrder> recOrders, ArrayList<RecOrder> oracle){
        ArrayList<RecOrder> differentQuantityOrders = new ArrayList<>();
        for(int i = 0; i < recOrders.size(); i++){
            if(recOrders.get(i).getQuantity() != oracle.get(i).getQuantity()){
                differentQuantityOrders.add(recOrders.get(i));
            }
        }
        return differentQuantityOrders;
    }


}
