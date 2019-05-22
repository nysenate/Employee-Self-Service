package gov.nysenate.ess.core.client.view.pec.video;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskViewFactory;
import gov.nysenate.ess.core.model.pec.video.PECVideo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class PECVideoViewFactory implements PersonnelTaskViewFactory<PECVideo> {

    private final String pecVidResPath;

    public PECVideoViewFactory(
            @Value("${data.pecvid_subdir}") String pecVidSubdir,
            @Value("${resource.path}") String resPath) {
        this.pecVidResPath = Paths.get(resPath, pecVidSubdir).toString();
    }

    @Override
    public PECVideoView getView(PECVideo video) {
        return new PECVideoView(video, pecVidResPath);
    }

    @Override
    public Class<PECVideo> getTaskClass() {
        return PECVideo.class;
    }
}
