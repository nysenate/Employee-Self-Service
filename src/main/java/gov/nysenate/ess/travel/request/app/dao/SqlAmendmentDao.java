package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.lodging.SqlLodgingPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.SqlMealPerDiemsDao;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.request.route.RouteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlAmendmentDao extends SqlBaseDao {

    private RouteDao routeDao;
    private SqlAllowancesDao allowancesDao;
    private SqlMealPerDiemsDao mealPerDiemsDao;
    private SqlLodgingPerDiemsDao lodgingPerDiemsDao;
    private SqlAttachmentDao attachmentDao;
    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlAmendmentDao(RouteDao routeDao,
                           SqlAllowancesDao allowancesDao,
                           SqlMealPerDiemsDao mealPerDiemsDao,
                           SqlLodgingPerDiemsDao lodgingPerDiemsDao,
                           SqlAttachmentDao attachmentDao,
                           EmployeeInfoService employeeInfoService) {
        this.routeDao = routeDao;
        this.allowancesDao = allowancesDao;
        this.mealPerDiemsDao = mealPerDiemsDao;
        this.lodgingPerDiemsDao = lodgingPerDiemsDao;
        this.attachmentDao = attachmentDao;
        this.employeeInfoService = employeeInfoService;
    }

    public Amendment selectAmendment(AmendmentRepositoryView amdView) {
        Route route = routeDao.selectRoute(amdView.amendmentId);
        Allowances allowances = allowancesDao.selectAllowances(amdView.amendmentId);
        MealPerDiems mpds = mealPerDiemsDao.selectMealPerDiems(amdView.amendmentId);
        LodgingPerDiems lpds = lodgingPerDiemsDao.selectLodgingPerDiems(amdView.amendmentId);
        List<Attachment> attachments = attachmentDao.selectAttachments(amdView.amendmentId);
        Employee createdBy = employeeInfoService.getEmployee(amdView.createdByEmpId);
        return new Amendment.Builder()
                .withAmendmentId(amdView.amendmentId)
                .withVersion(amdView.version)
                .withPurposeOfTravel(amdView.pot)
                .withRoute(route)
                .withAllowances(allowances)
                .withMealPerDiems(mpds)
                .withLodgingPerDiems(lpds)
                .withAttachments(attachments)
                .withCreatedDateTime(amdView.createdDateTime)
                .withCreatedBy(createdBy)
                .build();
    }

    void saveAmendment(Amendment amd, int appId) {
        // If amendment id is set, its already in the database.
        // Amendments should never be modified so we can return.
        if (amd.amendmentId() != 0) {
            return;
        }
        insertAmendment(amd, appId);
        routeDao.saveRoute(amd.route(), amd.amendmentId());
        allowancesDao.saveAllowances(amd.allowances(), amd.amendmentId());
        mealPerDiemsDao.saveMealPerDiems(amd.mealPerDiems(), amd.amendmentId());
        lodgingPerDiemsDao.saveLodgingPerDiems(amd.lodgingPerDiems(), amd.amendmentId());
        attachmentDao.saveAttachments(amd.attachments(), amd.amendmentId());
    }

    private void insertAmendment(Amendment amd, int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("eventType", amd.purposeOfTravel().eventType().name())
                .addValue("eventName", amd.purposeOfTravel().eventName())
                .addValue("additionalPurpose", amd.purposeOfTravel().additionalPurpose())
                .addValue("createdBy", amd.createdBy().getEmployeeId())
                .addValue("version", amd.version().name());
        String sql = SqlTravelApplicationQuery.INSERT_AMENDMENT.getSql(schemaMap());
        KeyHolder kh = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, kh);
        amd.setAmendmentId((Integer) kh.getKeys().get("amendment_id"));
    }
}