package gov.nysenate.ess.core.service.pec.task;



import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.ethics.DateRangedEthicsCode;
import gov.nysenate.ess.core.model.pec.video.IncorrectPECVideoCodeEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PersonnelCodeVerificationService {

    private final PersonnelTaskDao personnelTaskDao;
    @Autowired
    public PersonnelCodeVerificationService(PersonnelTaskDao personnelTaskDao){
        this.personnelTaskDao=personnelTaskDao;
    }

    public void verifyDateRangedEthics(List<String> codeSubmission, String dateInput)throws IncorrectPECVideoCodeEx {
        List<DateRangedEthicsCode> codeList = personnelTaskDao.getEthicsCodes();
        Collections.sort(codeList);
        String code1 = codeSubmission.get(0);
        String code2 = codeSubmission.get(1);
        LocalDateTime codeDate = LocalDateTime.parse(dateInput.substring(0, 19));
        int matchedEntries = 0;

        for (DateRangedEthicsCode drec : codeList) {
            if (drec.getCode().equals(code1) && drec.getStartDate().isBefore(codeDate) && drec.getEndDate().isAfter(codeDate)) {
                matchedEntries++;
            } else if (drec.getCode().equals(code2) && drec.getStartDate().isBefore(codeDate) && drec.getEndDate().isAfter(codeDate)) {
                matchedEntries++;
            }
        }

        if (matchedEntries < 2) {
            throw new IncorrectPECVideoCodeEx();
        }

    }


}
