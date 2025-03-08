package gov.nysenate.ess.supply.socket;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RequisitionStompService {

    private final EventBus eventBus;
    @Autowired private AsyncRequisitionStomper requisitionStomper;

    @Autowired
    public RequisitionStompService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void handleRequisitionUpdateEvent(RequisitionUpdateEvent updateEvent) {
        requisitionStomper.broadcast(updateEvent.getRequisitionView());
    }

    @Service
    public static class AsyncRequisitionStomper {
        @Autowired private SimpMessagingTemplate messagingTemplate;

        private final String brokerName = "/event/requisition";

        @Async
        public void broadcast(RequisitionView requisition) {
            messagingTemplate.convertAndSend(brokerName, requisition);
        }
    }

}
