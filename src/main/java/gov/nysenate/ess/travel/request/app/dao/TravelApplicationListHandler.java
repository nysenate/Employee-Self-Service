package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TravelApplicationListHandler extends BaseHandler {

    private Map<Integer, TravelAppRepositoryView> idToApp;
    private AmendmentRowMapper amdRowMapper;
    private TravelApplicationRowMapper applicationRowMapper;

    TravelApplicationListHandler() {
        idToApp = new HashMap<>();
        amdRowMapper = new AmendmentRowMapper();
        applicationRowMapper = new TravelApplicationRowMapper();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        var appId = rs.getInt("app_id");
        if (idToApp.containsKey(appId)) {
            idToApp.get(appId).amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
        } else {
            var view = applicationRowMapper.mapRow(rs, rs.getRow());
            view.amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
            idToApp.put(appId, view);
        }
    }

    List<TravelAppRepositoryView> getApplications() {
        return new ArrayList<>(idToApp.values());
    }
}
