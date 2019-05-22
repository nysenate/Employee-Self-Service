package gov.nysenate.ess.core.dao.pec.video;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.video.PECVideo;
import gov.nysenate.ess.core.model.pec.video.PECVideoCode;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.dao.pec.video.SqlPECVideoQuery.*;

@Repository
public class SqlPECVideoDao extends SqlBaseDao implements PECVideoDao {

    @Override
    public List<PECVideo> getActiveVideos() {
        PECVideoRowHandler rowHandler = new PECVideoRowHandler();
        localNamedJdbc.query(GET_ACTIVE_PEC_VIDEOS.getSql(schemaMap()), rowHandler);
        return rowHandler.getVideos();
    }

    @Override
    public PECVideo getVideo(int videoId) throws PECVideoNotFoundEx {
        PECVideoRowHandler rowHandler = new PECVideoRowHandler();
        MapSqlParameterSource params = new MapSqlParameterSource("videoId", videoId);
        localNamedJdbc.query(GET_PEC_VIDEO_BY_ID.getSql(schemaMap()), params, rowHandler);
        List<PECVideo> videos = rowHandler.getVideos();
        if (videos.isEmpty()) {
            throw new PECVideoNotFoundEx(videoId);
        }
        if (videos.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(
                    "Too many PEC vids returned for id: " + videoId, 1, videos.size());
        }
        return videos.get(0);
    }

    private static final RowMapper<PECVideo.Builder> vidBuilderRowMapper = (rs, rowNum) ->
            PECVideo.builder()
                    .setVideoId(rs.getInt("pec_video_id"))
                    .setFilename(rs.getString("filename"))
                    .setTitle(rs.getString("title"));

    private static final RowMapper<PECVideoCode> vidCodeRowMapper = (rs, rowNum) ->
            new PECVideoCode(
                    rs.getInt("pec_video_id"),
                    rs.getInt("sequence_no"),
                    rs.getString("label"),
                    rs.getString("code")
            );

    private static class PECVideoRowHandler implements RowCallbackHandler {

        private TreeMap<Integer, PECVideo.Builder> builderMap = new TreeMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            PECVideoCode code = vidCodeRowMapper.mapRow(rs, rs.getRow());
            PECVideo.Builder pecVidBldr = builderMap.get(code.getVideoId());
            if (pecVidBldr == null) {
                pecVidBldr = vidBuilderRowMapper.mapRow(rs, rs.getRow());
                builderMap.put(code.getVideoId(), pecVidBldr);
            }
            pecVidBldr.addCode(code);
        }

        public List<PECVideo> getVideos() {
            return builderMap.values().stream()
                    .map(PECVideo.Builder::build)
                    .collect(Collectors.toList());
        }
    }

}
