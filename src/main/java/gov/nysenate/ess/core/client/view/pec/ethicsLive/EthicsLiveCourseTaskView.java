package gov.nysenate.ess.core.client.view.pec.ethicsLive;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.client.view.pec.video.PECVideoCodeView;
import gov.nysenate.ess.core.model.pec.ethics.EthicsLiveCourseTask;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EthicsLiveCourseTaskView extends PersonnelTaskView {

    private final String url;

    private final List<PECVideoCodeView> codes;

    public EthicsLiveCourseTaskView(EthicsLiveCourseTask ethicsLiveCourseTask) {
        super(ethicsLiveCourseTask);
        this.url = Objects.toString(ethicsLiveCourseTask.getCourseUrl());
        this.codes = ethicsLiveCourseTask.getCodes().stream()
                .map(PECVideoCodeView::new)
                .collect(Collectors.toList());
    }

    public String getUrl() {
        return url;
    }

    public List<PECVideoCodeView> getCodes() {
        return codes;
    }
}
