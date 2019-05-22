package gov.nysenate.ess.core.dao.pec.video;

public class PECVideoNotFoundEx extends RuntimeException {

    private final int videoId;

    public PECVideoNotFoundEx(int videoId) {
        super("Could not find a PEC Video with id: " + videoId);
        this.videoId = videoId;
    }

    public int getVideoId() {
        return videoId;
    }
}
