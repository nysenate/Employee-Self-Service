package gov.nysenate.ess.travel.request.allowances.mileage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.address.TravelAddressView;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.ModeOfTransportationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MileagePerDiemView implements ViewObject {
    private static final DateTimeFormatter DATEPICKER_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private int id;
    private TravelAddressView from;
    private TravelAddressView to;
    private ModeOfTransportationView modeOfTransportation;
    private String miles;
    private String travelDate;
    private String mileageRate;
    @JsonProperty("isOutbound")
    private boolean isOutbound;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;
    private boolean qualifiesForReimbursement;

    public MileagePerDiemView() {}

    public MileagePerDiemView(MileagePerDiem mpd) {
        this.id = mpd.getId();
        this.from = new TravelAddressView(mpd.getFrom());
        this.to = new TravelAddressView(mpd.getTo());
        this.modeOfTransportation = new ModeOfTransportationView(mpd.getModeOfTransportation());
        this.miles = BigDecimal.valueOf(mpd.getMiles()).setScale(1, RoundingMode.HALF_UP).toString();
        this.travelDate = mpd.getTravelDate().format(DATEPICKER_FORMAT);
        this.mileageRate = mpd.getMileageRate().toString();
        this.isOutbound = mpd.isOutbound();
        this.isReimbursementRequested = mpd.isReimbursementRequested();
        this.requestedPerDiem = mpd.requestedPerDiemValue().toString();
        this.maximumPerDiem = mpd.perDiemValue().toString();
        this.qualifiesForReimbursement = mpd.qualifiesForReimbursement();
    }

    public MileagePerDiem toMileagePerDiem() {
        return new MileagePerDiem(
                id,
                from.toTravelAddress(),
                to.toTravelAddress(),
                modeOfTransportation.toModeOfTransportation(),
                miles == null ? Double.valueOf(0) : Double.valueOf(miles),
                new PerDiem(LocalDate.parse(travelDate, DATEPICKER_FORMAT), mileageRate == null ? BigDecimal.ZERO : new BigDecimal(mileageRate)),
                isOutbound,
                isReimbursementRequested
        );
    }

    public int getId() {
        return id;
    }

    public TravelAddressView getFrom() {
        return from;
    }

    public TravelAddressView getTo() {
        return to;
    }

    public ModeOfTransportationView getModeOfTransportation() {
        return modeOfTransportation;
    }

    public String getMiles() {
        return miles;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getMileageRate() {
        return mileageRate;
    }

    @JsonIgnore
    public boolean isOutbound() {
        return isOutbound;
    }

    @JsonIgnore
    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    public boolean isQualifiesForReimbursement() {
        return qualifiesForReimbursement;
    }

    @Override
    public String getViewType() {
        return "mileage-per-diem";
    }
}
