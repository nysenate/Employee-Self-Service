package gov.nysenate.ess.core.service.pec.task;



import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.ethics.DateRangedEthicsCode;
import gov.nysenate.ess.core.model.pec.video.IncorrectPECVideoCodeEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PersonnelCodeVerificationService {

    private final PersonnelTaskDao personnelTaskDao;
    @Autowired
    public PersonnelCodeVerificationService(PersonnelTaskDao personnelTaskDao){
        this.personnelTaskDao = personnelTaskDao;
    }

    public void verifyDateRangedEthics(List<String> codeSubmission, String dateInput)throws IncorrectPECVideoCodeEx {
        //History of codes
        List<DateRangedEthicsCode> codeList = personnelTaskDao.getEthicsCodes();
        Collections.sort(codeList);

        //User submitted codes
        String code1 = codeSubmission.get(0).toUpperCase();
        String code2 = codeSubmission.get(1).toUpperCase();
        LocalDateTime parsedDate = LocalDateTime.parse(dateInput.substring(0, 19));
        LocalDate codeDate = parsedDate.toLocalDate();
        int matchedEntries = 0;

        //Cycle thru the history of codes to see if 2 codes match and are within the correct date range
        for (DateRangedEthicsCode drec : codeList) {

            if (drec.getStartDate() == null || drec.getEndDate() == null) {
                continue;
            }

            LocalDate drecStartDate = drec.getStartDate().toLocalDate();
            LocalDate drecEndDate = drec.getEndDate().toLocalDate();

            if (drec.getCode().equals(code1) && ( (drecStartDate.isBefore(codeDate) && drecEndDate.isAfter(codeDate))
                    || drecStartDate.equals(codeDate) || drecEndDate.equals(codeDate) ) ) {
                matchedEntries++;
            }
            else if (drec.getCode().equals(code2) && ( (drecStartDate.isBefore(codeDate) && drecEndDate.isAfter(codeDate))
                    || drecStartDate.equals(codeDate) || drecEndDate.equals(codeDate) ) ) {
                matchedEntries++;
            }
        }

        //1 or more incorrect codes so error out
        if (matchedEntries < 2) {
            throw new IncorrectPECVideoCodeEx();
        }

    }


}
