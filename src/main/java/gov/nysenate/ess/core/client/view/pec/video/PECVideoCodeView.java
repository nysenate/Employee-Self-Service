package gov.nysenate.ess.core.client.view.pec.video;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.video.VideoTaskCode;

/**
 * View representation of {@link VideoTaskCode}
 *
 * The code value is not provided to thwart cheaters.
 */
public class PECVideoCodeView implements ViewObject {
    private final int videoId;
    private final int sequenceNo;
    private final String label;

    public PECVideoCodeView(VideoTaskCode code) {
        this.videoId = code.getVideoId();
        this.sequenceNo = code.getSequenceNo();
        this.label = code.getLabel();
    }

    @Override
    public String getViewType() {
        return "PEC-video-code";
    }

    public int getVideoId() {
        return videoId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getLabel() {
        return label;
    }
}
