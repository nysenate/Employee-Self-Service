package gov.nysenate.ess.core.model.pec.video;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * A {@link PersonnelTask} in which employees need to verify that they watched a video by entering one or more codes.
 */
public class VideoTask extends PersonnelTask {

    private final String filename;

    private final ImmutableList<VideoTaskCode> codes;

    public VideoTask(PersonnelTask task, String filename, Collection<VideoTaskCode> codes) {
        super(task);
        this.filename = filename;
        this.codes = Optional.ofNullable(codes)
                .orElse(Collections.emptyList())
                .stream()
                .sorted()
                .collect(Collectors.collectingAndThen(toList(), ImmutableList::copyOf));
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
                .map(VideoTaskCode::getCode)
                .collect(toList());

        if (!expectedCodes.equals(codeSubmission)) {
            throw new IncorrectPECVideoCodeEx();
        }
    }

    /* --- Getters --- */

    public String getFilename() {
        return filename;
    }

    public ImmutableList<VideoTaskCode> getCodes() {
        return codes;
    }
}
