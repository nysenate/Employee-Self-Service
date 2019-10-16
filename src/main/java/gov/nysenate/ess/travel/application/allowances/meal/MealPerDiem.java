package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class MealPerDiem {

    private int id;
    private final GoogleAddress address;
    private final LocalDate date;
    private final BigDecimal rate;
    private boolean isReimbursementRequested;

    public MealPerDiem(GoogleAddress address, PerDiem perDiem) {
        this(0, address, perDiem, true);
    }

    public MealPerDiem(int id, GoogleAddress address, PerDiem perDiem, boolean isReimbursementRequested) {
        this.id = id;
        this.address = address;
        this.date = perDiem.getDate();
        this.rate = perDiem.getRate();
        this.isReimbursementRequested = isReimbursementRequested;
    }

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dollars maximumPerDiem() {
        return rate();
    }

    public Dollars requestedPerDiem() {
        return isReimbursementRequested() ? maximumPerDiem() : Dollars.ZERO;
    }

    public GoogleAddress address() {
        return address;
    }

    public LocalDate date() {
        return date;
    }

    public Dollars rate() {
        return new Dollars(rate);
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    // TODO toString, equals, hashcode
}
