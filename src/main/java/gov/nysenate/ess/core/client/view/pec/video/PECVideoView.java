package gov.nysenate.ess.core.client.view.pec.video;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.video.VideoTask;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class PECVideoView extends PersonnelTaskView {

    private final String path;

    private final List<PECCodeView> codes;

    public PECVideoView(VideoTask video, String pecVidResPath) {
        super(video);
        this.path = Paths.get(pecVidResPath, video.getFilename()).toString();
        this.codes = video.getCodes().stream()
                .map(PECCodeView::new)
                .collect(Collectors.toList());
    }

    public String getPath() {
        return path;
    }

    public List<PECCodeView> getCodes() {
        return codes;
    }
}
