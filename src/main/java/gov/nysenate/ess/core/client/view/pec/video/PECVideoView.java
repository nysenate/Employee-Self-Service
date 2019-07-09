package gov.nysenate.ess.core.client.view.pec.video;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskIdView;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.video.PECVideo;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.VIDEO_CODE_ENTRY;

public class PECVideoView implements PersonnelTaskView {

    private final int videoId;
    private final String title;
    private final String path;
    private final boolean active;

    private final List<PECVideoCodeView> codes;

    public PECVideoView(PECVideo video, String pecVidResPath) {
        this.videoId = video.getVideoId();
        this.title = video.getTitle();
        this.path = Paths.get(pecVidResPath, video.getFilename()).toString();
        this.active = video.isActive();
        this.codes = video.getCodes().stream()
                .map(PECVideoCodeView::new)
                .collect(Collectors.toList());
    }

    @Override
    public PersonnelTaskIdView getTaskId() {
        return new PersonnelTaskIdView(VIDEO_CODE_ENTRY, videoId);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getPath() {
        return path;
    }

    public List<PECVideoCodeView> getCodes() {
        return codes;
    }
}
