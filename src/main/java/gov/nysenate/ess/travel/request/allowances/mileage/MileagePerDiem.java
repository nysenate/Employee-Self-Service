package gov.nysenate.ess.travel.request.allowances.mileage;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.ModeOfTransportation;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MileagePerDiem {
    private int id;
    private final TravelAddress from;
    private final TravelAddress to;
    private final ModeOfTransportation modeOfTransportation;
    private final double miles;
    private final PerDiem mileagePerDiem;
    private final boolean isOutbound;
    private final boolean isReimbursementRequested;

    public MileagePerDiem(int id, TravelAddress from, TravelAddress to, ModeOfTransportation modeOfTransportation,
                          double miles, PerDiem mileagePerDiem, boolean isOutbound, boolean isReimbursementRequested) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.modeOfTransportation = modeOfTransportation;
        this.miles = miles;
        this.mileagePerDiem = mileagePerDiem;
        this.isOutbound = isOutbound;
        this.isReimbursementRequested = isReimbursementRequested;
    }

    /**
     * The total requested per diem value.
     */
    public Dollars requestedPerDiemValue() {
        return isReimbursementRequested()
                ? perDiemValue()
                : Dollars.ZERO;
    }

    /**
     * The standard mileage reimbursement value for this per diem.
     *
     * @return
     */
    protected Dollars perDiemValue() {
        return qualifiesForReimbursement()
                ? new Dollars(getMileageRate().multiply(new BigDecimal(miles)))
                : Dollars.ZERO;
    }

    protected boolean qualifiesForReimbursement() {
        return modeOfTransportation.qualifiesForMileageReimbursement();
    }

    protected boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    protected LocalDate getTravelDate() {
        return mileagePerDiem.getDate();
    }

    public BigDecimal getMileageRate() {
        return mileagePerDiem.getRate();
    }

    public double getMiles() {
        return miles;
    }

    protected boolean isOutbound() {
        return isOutbound;
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public TravelAddress getFrom() {
        return from;
    }

    public TravelAddress getTo() {
        return to;
    }

    protected ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }
}
