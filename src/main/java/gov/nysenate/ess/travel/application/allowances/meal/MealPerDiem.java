package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;

public final class MealPerDiem {

    private int id;
    private final GoogleAddress address;
    private final LocalDate date;
    private final GsaMie mie;
    private boolean isReimbursementRequested;

    public MealPerDiem(GoogleAddress address, LocalDate date, GsaMie mie) {
        this(0, address, date, mie, true);
    }

    public MealPerDiem(int id, GoogleAddress address, LocalDate date, GsaMie mie, boolean isReimbursementRequested) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.mie = mie;
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

    public GsaMie mie() {
        return mie;
    }

    public GoogleAddress address() {
        return address;
    }

    public LocalDate date() {
        return date;
    }

    public Dollars rate() {
        return mie.total();
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    // TODO toString, equals, hashcode
}
