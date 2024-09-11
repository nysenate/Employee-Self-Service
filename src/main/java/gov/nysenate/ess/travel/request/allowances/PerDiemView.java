package gov.nysenate.ess.travel.request.allowances;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PerDiemView implements ViewObject {

    private String date;
    private String rate;

    public PerDiemView() {
    }

    public PerDiemView(PerDiem pd) {
        this.date = pd.getDate().format(DateTimeFormatter.ISO_DATE);
        this.rate = pd.getRate().toString();
    }

    public PerDiem toPerDiem() {
        return new PerDiem(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), new BigDecimal(rate));
    }

    public String getDate() {
        return date;
    }

    public String getRate() {
        return rate;
    }

    @Override
    public String getViewType() {
        return "per-diem";
    }
}
