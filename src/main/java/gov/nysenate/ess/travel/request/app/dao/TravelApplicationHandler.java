package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

class TravelApplicationHandler extends BaseHandler {

    private TravelAppRepositoryView view;
    private AmendmentRowMapper amdRowMapper;
    private TravelApplicationRowMapper applicationRowMapper;

    public TravelApplicationHandler() {
        amdRowMapper = new AmendmentRowMapper();
        applicationRowMapper = new TravelApplicationRowMapper();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        view = applicationRowMapper.mapRow(rs, rs.getRow());
        view.amendmentViews.add(amdRowMapper.mapRow(rs, rs.getRow()));
    }

    public TravelAppRepositoryView results() {
        return view;
    }
}
