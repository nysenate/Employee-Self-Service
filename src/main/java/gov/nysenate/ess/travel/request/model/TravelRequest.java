package gov.nysenate.ess.travel.request.model;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TravelRequest {

    private Employee travelingEmployee;
    private ModeOfTransportation modeOfTransportation;

    private BigDecimal lodgingReimbursement;
    private BigDecimal mealReimbursement;
    private BigDecimal travelReimbursement;

    private Employee submittedBy;
    private LocalDateTime submittedDateTime;
    private Employee modifiedBy;
    private LocalDateTime modifiedDateTime;
}
