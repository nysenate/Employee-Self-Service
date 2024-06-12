package gov.nysenate.ess.core.model.pec.ethics;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.IncorrectCodeException;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.video.VideoTaskCode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class EthicsLiveCourseTask extends PersonnelTask {
    private final URL courseUrl;

    //Reusing Video codes because nothing would be changed other than naming for this kind of task
    private final ImmutableList<VideoTaskCode> codes;

    public EthicsLiveCourseTask(PersonnelTask task, Collection<VideoTaskCode>codes) {
        super(task);

        try {
            this.courseUrl = new URL(task.getUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid ethics course url string: \"" + task.getUrl() + "\"", e);
        }
        this.codes = Optional.ofNullable(codes)
                .orElse(Collections.emptyList())
                .stream()
                .sorted()
                .collect(Collectors.collectingAndThen(toList(), ImmutableList::copyOf));
    }

    /**
     * Verify the given codes, throwing an exception if they don't match the codes for this video
     *
     * @param codeSubmission List<String>
     * @throws IncorrectCodeException if the codes are wrong.
     */
    public void verifyCodes(List<String> codeSubmission) throws IncorrectCodeException {
        List<String> expectedCodes = codes.stream()
                .sorted()
                .map(VideoTaskCode::getCode)
                .collect(toList());

        if (!expectedCodes.equals(codeSubmission)) {
            throw new IncorrectCodeException();
        }
    }

    public ImmutableList<VideoTaskCode> getCodes() {
        return codes;
    }

    public URL getCourseUrl() {
        return courseUrl;
    }
}
