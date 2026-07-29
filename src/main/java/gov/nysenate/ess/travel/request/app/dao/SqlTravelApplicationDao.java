package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.request.allowances.SqlAllowancesDao;
import gov.nysenate.ess.travel.request.allowances.lodging.SqlLodgingPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.meal.SqlMealPerDiemsDao;
import gov.nysenate.ess.travel.request.allowances.mileage.SqlMileagePerDiemsDao;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.route.RouteDao;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Repository
public class SqlTravelApplicationDao extends SqlBaseDao implements TravelApplicationDao {

    private Logger logger = LoggerFactory.getLogger(SqlTravelApplicationDao.class);

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteDao routeDao;
    @Autowired private SqlAllowancesDao allowancesDao;
    @Autowired private SqlMealPerDiemsDao mealPerDiemsDao;
    @Autowired private SqlLodgingPerDiemsDao sqlLodgingPerDiemsDao;
    @Autowired private SqlMileagePerDiemsDao mileagePerDiemsDao;
    @Autowired private SqlAttachmentDao attachmentDao;

    /**
     * Persists a {@link TravelApplication} to the database.
     * <p>
     * SQL Updates should not be made to the TravelApplication or any of its data.
     * Changes to an application are done by creating and inserting a new amendment only.
     * <p>
     * This method will check the id's of an application and its amendments to see if
     * they exist in the database. Applications and amendments existing in the database
     * will be ignored.
     * If:
     * id == 0:   Will be inserted into the database.
     * id != 0:   Will be ignored.
     *
     * @param app
     */
    @Override
    @Transactional(value = "localTxManager")
    public synchronized void saveTravelApplication(TravelApplication app) {
        saveApplication(app);
        routeDao.saveRoute(app.getRoute(), app.getAppId());
        allowancesDao.saveAllowances(app.getAllowances(), app.getAppId());
        mealPerDiemsDao.updateMealPerDiems(app.getMealPerDiems(), app.getAppId());
        sqlLodgingPerDiemsDao.updateLodgingPerDiems(app.getLodgingPerDiems(), app.getAppId());
        mileagePerDiemsDao.updateMileagePerDiems(app.getMileagePerDiems(), app.getAppId());
        attachmentDao.saveTravelAppAttachments(app.getAttachments(), app.getAppId());
    }

    @Override
    public void updateTravelApplicationStatus(int appId, TravelApplicationStatus status) {
       MapSqlParameterSource params = new MapSqlParameterSource()
               .addValue("appId", appId)
               .addValue("status", status.status().name())
               .addValue("note", status.note());
       String sql = SqlTravelApplicationQuery.UPDATE_APP_STATUS.getSql(schemaMap());
       localNamedJdbc.update(sql, params);
    }

    private void saveApplication(TravelApplication app) {
        if (updateApplication(app) == 0) {
            insertApplication(app);
        }
    }

    private int updateApplication(TravelApplication app) {
        MapSqlParameterSource params = travelAppParams(app);
        String sql = SqlTravelApplicationQuery.UPDATE_APP.getSql(schemaMap());
        return localNamedJdbc.update(sql, params);
    }

    /*
     * Inserts the app into the database and sets its appId.
     */
    private void insertApplication(TravelApplication app) {
        MapSqlParameterSource params = travelAppParams(app);
        String insertSql = SqlTravelApplicationQuery.INSERT_APP.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(insertSql, params, keyHolder);
        app.setAppId((Integer) keyHolder.getKeys().get("app_id"));
    }

    @Override
    public TravelApplication selectTravelApplication(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlTravelApplicationQuery.SELECT_APP_BY_ID.getSql(schemaMap());
        TravelAppRepositoryView appRepView = localNamedJdbc.queryForObject(sql, params, new TravelApplicationRowMapper());
        return populateApplicationDetails(appRepView);
    }

    @Override
    public List<TravelApplication> selectAllApplications(LocalDateTime fromDate, LocalDateTime toDate) {
        Map<String, ?> paramsMap = Map.ofEntries(
                entry("fromDate", fromDate),
                entry("toDate", toDate)
        );
        MapSqlParameterSource params = new MapSqlParameterSource(paramsMap);
        String sql = SqlTravelApplicationQuery.SELECT_APPS_BY_FROM_AND_TO_DATES.getSql(schemaMap());
        List<TravelAppRepositoryView> appRepViews = localNamedJdbc.query(sql, params, new TravelApplicationRowMapper());
        return appRepViews.stream()
                .map(this::populateApplicationDetails)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedList<TravelApplication> selectTravelApplications(int userId, TravelApplicationQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("statuses", query.getStatuses().stream().map(Enum::name).toList())
                .addValue("fromDate", query.getFrom(), Types.DATE)
                .addValue("toDate", query.getTo(), Types.DATE);

        int total = localNamedJdbc.queryForObject(
                SqlTravelApplicationQuery.COUNT_APPS_FOR_USER.getSql(schemaMap()),
                params,
                Integer.class
        );

        Map<String, SortOrder> sortColumns = new LinkedHashMap<>();
        sortColumns.put(query.getSortField().sqlColumn(), query.getSortOrder());
        // Use the unique app ID as a tie-breaker so pagination has a stable, deterministic order.
        if (query.getSortField() != TravelApplicationSortField.ID) {
            sortColumns.put(TravelApplicationSortField.ID.sqlColumn(), query.getSortOrder());
        }
        String sql = SqlTravelApplicationQuery.SELECT_APPS_FOR_USER.getSql(
                schemaMap(),
                new OrderBy(sortColumns),
                query.getLimitOffset()
        );

        List<TravelAppRepositoryView> appRepViews =
                localNamedJdbc.query(sql, params, new TravelApplicationRowMapper());
        List<TravelApplication> applications = appRepViews.stream()
                .map(this::populateApplicationDetails)
                .collect(Collectors.toList());
        return new PaginatedList<>(total, query.getLimitOffset(), applications);
    }

    private TravelApplication populateApplicationDetails(TravelAppRepositoryView view) {
        Employee traveler = employeeInfoService.getEmployee(view.travelerEmpId);
        return new TravelApplication.Builder(traveler, view.travelerDeptHeadEmpId)
                .withAppId(view.appId)
                .withPurposeOfTravel(view.pot)
                .withRoute(routeDao.selectRoute(view.appId))
                .withAllowances(allowancesDao.selectAllowances(view.appId))
                .withAttachments(attachmentDao.selectAttachments(view.appId))
                .withMealPerDiems(mealPerDiemsDao.selectMealPerDiems(view.appId))
                .withLodgingPerDiems(sqlLodgingPerDiemsDao.selectLodgingPerDiems(view.appId))
                .withMileagePerDiems(mileagePerDiemsDao.selectMileagePerDiems(view.appId))
                .withStatus(view.status)
                .withCreatedBy(employeeInfoService.getEmployee(view.createdByEmpId))
                .withModifiedBy(employeeInfoService.getEmployee(view.modifiedByEmpId))
                .withCreatedDateTime(view.submittedDateTime)
                .withModifiedDateTime(view.modifiedDateTime)
                .build();
    }

    private MapSqlParameterSource travelAppParams(TravelApplication app) {
        return new MapSqlParameterSource()
                .addValue("appId", app.getAppId())
                .addValue("travelerId", app.getTraveler().getEmployeeId())
                .addValue("travelerDeptHeadEmpId", app.getTravelerDeptHeadEmpId())
                .addValue("submittedById", app.getCreatedBy().getEmployeeId())
                .addValue("status", app.getStatus().status().name())
                .addValue("note", app.getStatus().note())
                .addValue("eventType", app.getPurposeOfTravel().eventType().name())
                .addValue("eventName", app.getPurposeOfTravel().eventName())
                .addValue("additionalPurpose", app.getPurposeOfTravel().additionalPurpose())
                .addValue("modifiedBy", app.getModifiedBy().getEmployeeId())
                .addValue("modifiedDateTime", toDate(LocalDateTime.now()));
    }
}
