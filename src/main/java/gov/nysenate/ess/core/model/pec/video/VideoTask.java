package gov.nysenate.ess.core.model.pec.video;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.IncorrectCodeException;
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

    private final ImmutableList<VideoTaskCode> codes;

    public VideoTask(PersonnelTask task, Collection<VideoTaskCode> codes) {
        super(task);
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
     * @throws IncorrectCodeException if the codes are wrong.
     */
    public void verifyCodes(List<String> codeSubmission) throws IncorrectCodeException {
        List<String> expectedCodes = codes.stream()
                .sorted()
                .map(VideoTaskCode::getCode).toList();

        if (!expectedCodes.equals(codeSubmission)) {
            throw new IncorrectCodeException();
        }
    }

    /* --- Getters --- */

    public String getFilename() {
        return super.getResource();
    }

    public ImmutableList<VideoTaskCode> getCodes() {
        return codes;
    }
}
