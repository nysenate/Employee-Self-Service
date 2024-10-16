package gov.nysenate.ess.core.client.view.pec.ethicsLive;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.client.view.pec.video.PECCodeView;
import gov.nysenate.ess.core.model.pec.ethics.EthicsLiveCourseTask;

import java.util.List;
import java.util.stream.Collectors;

public class EthicsLiveCourseTaskView extends PersonnelTaskView {

    private final List<PECCodeView> codes;

    public EthicsLiveCourseTaskView(EthicsLiveCourseTask ethicsLiveCourseTask) {
        super(ethicsLiveCourseTask);
        this.codes = ethicsLiveCourseTask.getCodes().stream()
                .map(PECCodeView::new)
                .collect(Collectors.toList());
    }

    public String getUrl() {
        return super.getUrl();
    }

    public List<PECCodeView> getCodes() {
        return codes;
    }
}
