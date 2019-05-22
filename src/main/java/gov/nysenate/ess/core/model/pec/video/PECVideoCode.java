package gov.nysenate.ess.core.model.pec.video;

import com.google.common.base.Objects;

/**
 * A verification code for a {@link PECVideo}
 */
public class PECVideoCode implements Comparable<PECVideoCode>{

    /** Video this code belongs to */
    private final int videoId;
    /** Ordering of the code within the context of the video. */
    private final int sequenceNo;
    /** Label associated with code field */
    private final String label;
    /** The value of the code */
    private final String code;

    public PECVideoCode(int videoId, int sequenceNo, String label, String code) {
        this.videoId = videoId;
        this.sequenceNo = sequenceNo;
        this.label = label;
        this.code = code;
    }

    /* --- Overrides --- */

    @Override
    public int compareTo(PECVideoCode o) {
        return Integer.compare(this.sequenceNo, o.sequenceNo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PECVideoCode)) return false;
        PECVideoCode that = (PECVideoCode) o;
        return sequenceNo == that.sequenceNo &&
                Objects.equal(label, that.label) &&
                Objects.equal(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sequenceNo, label, code);
    }

    /* --- Getters --- */

    public int getVideoId() {
        return videoId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }
}
