package gov.nysenate.ess.core.dao.pec.video;

import gov.nysenate.ess.core.model.pec.video.PECVideo;

import java.util.List;

/**
 * Dao for retrieving {@link PECVideo} information.
 */
public interface PECVideoDao {

    /**
     * Get a list of all currently active videos.
     * @return {@link List<PECVideo>}
     */
    List<PECVideo> getActiveVideos();

    /**
     * Get a video for a video number
     * @param videoId int
     * @return {@link PECVideo}
     * @throws PECVideoNotFoundEx if the video was not found.
     */
    PECVideo getVideo(int videoId) throws PECVideoNotFoundEx;
}
