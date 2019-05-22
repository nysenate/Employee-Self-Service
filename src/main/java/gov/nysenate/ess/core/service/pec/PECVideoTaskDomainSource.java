package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.pec.video.PECVideoDao;
import gov.nysenate.ess.core.dao.pec.video.PECVideoNotFoundEx;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PECVideo;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PECVideoTaskDomainSource implements PersonnelTaskDomainSource<PECVideo> {

    private final PECVideoDao pecVideoDao;

    public PECVideoTaskDomainSource(PECVideoDao pecVideoDao) {
        this.pecVideoDao = pecVideoDao;
    }

    @Override
    public PersonnelTaskType getTaskType() {
        return PersonnelTaskType.VIDEO_CODE_ENTRY;
    }

    @Override
    public Set<PersonnelTaskId> getActiveTaskIds() {
        return pecVideoDao.getActiveVideos().stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
    }

    @Override
    public PECVideo getPersonnelTask(int videoId) throws PersonnelTaskNotFoundEx {
        try {
            return pecVideoDao.getVideo(videoId);
        } catch (PECVideoNotFoundEx ex) {
            throw new PersonnelTaskNotFoundEx(new PersonnelTaskId(
                    PersonnelTaskType.VIDEO_CODE_ENTRY, videoId
            ));
        }
    }
}
