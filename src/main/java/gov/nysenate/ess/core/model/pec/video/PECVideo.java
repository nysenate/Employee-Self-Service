package gov.nysenate.ess.core.model.pec.video;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.VIDEO_CODE_ENTRY;
import static java.util.stream.Collectors.toList;

/**
 * A {@link PersonnelTask} for which employees need to verify that they watched a video by entering one or more codes.
 */
public class PECVideo implements PersonnelTask {

    private final int videoId;
    private final String title;
    private final String filename;
    private final boolean active;

    private final ImmutableList<PECVideoCode> codes;

    private PECVideo(Builder builder) {
        this.videoId = builder.videoId;
        this.title = builder.title;
        this.filename = builder.filename;
        this.active = builder.active;
        this.codes = Optional.ofNullable(builder.codes)
                .orElse(Collections.emptyList())
                .stream()
                .sorted()
                .collect(Collectors.collectingAndThen(toList(), ImmutableList::copyOf));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int videoId;
        private String title;
        private String filename;
        private List<PECVideoCode> codes = new ArrayList<>();
        private boolean active = true;

        private Builder() {}

        public PECVideo build() {
            return new PECVideo(this);
        }

        public Builder setVideoId(int videoId) {
            this.videoId = videoId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder setCodes(List<PECVideoCode> codes) {
            this.codes = codes;
            return this;
        }

        public Builder addCode(PECVideoCode code) {
            this.codes.add(code);
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }
    }

    /* --- Methods --- */

    /**
     * Verify the given codes, throwing an exception if they don't match the codes for this video
     *
     * @param codeSubmission List<String>
     * @throws IncorrectPECVideoCodeEx if the codes are wrong.
     */
    public void verifyCodes(List<String> codeSubmission) throws IncorrectPECVideoCodeEx {
        List<String> expectedCodes = codes.stream()
                .sorted()
                .map(PECVideoCode::getCode)
                .collect(toList());

        if (!expectedCodes.equals(codeSubmission)) {
            throw new IncorrectPECVideoCodeEx();
        }
    }

    /* --- Getters --- */

    @Override
    public PersonnelTaskId getTaskId() {
        return new PersonnelTaskId(VIDEO_CODE_ENTRY, videoId);
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

    public String getFilename() {
        return filename;
    }

    public ImmutableList<PECVideoCode> getCodes() {
        return codes;
    }
}
