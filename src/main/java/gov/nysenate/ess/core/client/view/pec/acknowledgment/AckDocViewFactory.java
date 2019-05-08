package gov.nysenate.ess.core.client.view.pec.acknowledgment;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskViewFactory;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class AckDocViewFactory implements PersonnelTaskViewFactory<AckDoc> {

    private final String ackDocResPath;

    public AckDocViewFactory(
            @Value("${data.ackdoc_subdir}") String ackDocSubdir,
            @Value("${resource.path}") String resPath) {
        this.ackDocResPath = Paths.get(resPath, ackDocSubdir).toString();
    }

    @Override
    public PersonnelTaskView getView(AckDoc ackDoc) {
        return new AckDocView(ackDoc, ackDocResPath);
    }

    @Override
    public Class<AckDoc> getTaskClass() {
        return AckDoc.class;
    }
}
