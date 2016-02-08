package gov.nysenate.ess.supply;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.supply.config.SupplyConfig;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItemDao;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.InMemoryOrderDao;
import gov.nysenate.ess.supply.order.service.OrderService;
import gov.nysenate.ess.supply.sfms.SfmsLineItem;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.SfmsOrderId;
import gov.nysenate.ess.supply.sfms.dao.InMemorySfmsOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@ContextConfiguration(classes = SupplyConfig.class)
public abstract class SupplyTests extends BaseTests {

    @Autowired private SupplyItemService itemService;
    @Autowired private InMemorySupplyItemDao itemDao;
    @Autowired private OrderService orderService;
    @Autowired private InMemoryOrderDao orderDao;
    @Autowired private InMemorySfmsOrderDao sfmsOrderDao;

    protected static final Range<LocalDate> ONE_DAY_RANGE = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
    protected static final Range<LocalDate> ONE_WEEK_RANGE = Range.closed(LocalDate.now().minusWeeks(1), LocalDate.now());
    protected static final Range<LocalDate> THREE_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(3), LocalDate.now());
    protected static final Range<LocalDate> SIX_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(6), LocalDate.now());

    protected static final int CUSTOMER_EMP_ID = 6221;
    protected static final int ISSUING_EMP_ID = 11168;
    protected static final int MODIFIED_EMP_ID = 10008;

    protected Set<LineItem> PENCILS_LGCLIPS_PAPERCLIPS;

    @PostConstruct
    private void setup() {
        PENCILS_LGCLIPS_PAPERCLIPS = initPencilsLgClipsPaperClips();
    }

    protected void resetInMemoryDaos() {
        itemDao.reset();
        orderDao.reset();
        sfmsOrderDao.reset();
    }

    protected Order createPendingOrder(Set<LineItem> items, int customerEmpId) {
        return orderService.submitOrder(items, customerEmpId, MODIFIED_EMP_ID);
    }

    protected Order createProcessingOrder(Set<LineItem> items, int customerEmpId, int issuingEmpId) {
        return orderService.processOrder(createPendingOrder(items, customerEmpId).getId(), issuingEmpId, MODIFIED_EMP_ID);
    }

    protected Order createCompletedOrder(Set<LineItem> items, int customerEmpId, int issuingEmpId) {
        return orderService.completeOrder(createProcessingOrder(items, customerEmpId, issuingEmpId).getId(), MODIFIED_EMP_ID);
    }

    protected Set<LineItem> initPencilsLgClipsPaperClips() {
        Set<LineItem> lineItems = new HashSet<>();
        lineItems.add(new LineItem(itemService.getItemById(1), 1));
        lineItems.add(new LineItem(itemService.getItemById(4), 3));
        lineItems.add(new LineItem(itemService.getItemById(6), 7));
        return lineItems;
    }
}
