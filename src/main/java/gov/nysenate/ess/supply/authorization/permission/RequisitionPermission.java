package gov.nysenate.ess.supply.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.web.bind.annotation.RequestMethod;

public class RequisitionPermission {

    private static final String DOMAIN = "supply";
    private static final String CUSTOMER = "customer";
    private static final String DESTINATION = "destination";
    private static final String REQ = "requisition";

    /**
     * Creates a permission for requisitions with the given customer id and action.
     * @param customerId
     * @param action
     * @return
     */
    public static WildcardPermission forCustomer(int customerId, RequestMethod action) {
        return new WildcardPermission(prefix(action) + CUSTOMER + ":" + String.valueOf(customerId));
    }

    /**
     * Creates a permission for requisitions with the given destination id and action.
     * @param locId The String representation of a locationId. i.e. A403-W
     * @param action
     * @return
     */
    public static WildcardPermission forDestination(String locId, RequestMethod action) {
        return new WildcardPermission(prefix(action) + DESTINATION + ":" + locId);
    }

    /**
     * Grants the given action on all requisitions.
     * @param action
     * @return
     */
    public static WildcardPermission forAll(RequestMethod action) {
        return new WildcardPermission(prefix(action));
    }

    private static String prefix(RequestMethod action) {
        return DOMAIN + ":" + REQ + ":" + action + ":";
    }
}
