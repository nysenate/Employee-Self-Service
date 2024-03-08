package gov.nysenate.ess.core.model.pec.ethics;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.video.IncorrectPECCodeEx;
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

    public EthicsLiveCourseTask(PersonnelTask task, String urlString, Collection<VideoTaskCode>codes) {
        super(task);

        try {
            this.courseUrl = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid ethics course url string: \"" + urlString + "\"", e);
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
     * @throws IncorrectPECCodeEx if the codes are wrong.
     */
    public void verifyCodes(List<String> codeSubmission) throws IncorrectPECCodeEx {
        List<String> expectedCodes = codes.stream()
                .sorted()
                .map(VideoTaskCode::getCode)
                .collect(toList());

        if (!expectedCodes.equals(codeSubmission)) {
            throw new IncorrectPECCodeEx();
        }
    }

    public ImmutableList<VideoTaskCode> getCodes() {
        return codes;
    }

    public URL getCourseUrl() {
        return courseUrl;
    }
}
