package gov.nysenate.ess.core.controller.api.pec;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/everfi")
public class EverfiApiCtrl extends BaseRestApiCtrl {

    private EverfiRecordService everfiRecordService;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private EverfiUserService everfiUserService;
    private EverfiCategoryService everfiCategoryService;
    final LocalDateTime jan1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
    final LocalDateTime lastYearJan = LocalDateTime.of(LocalDateTime.now().getYear() - 1, 1, 1, 0, 0, 1);
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime threeMonthsAgo = now.minusDays(90);
    private static final Logger logger = LoggerFactory.getLogger(EverfiApiCtrl.class);

    @Autowired
    public EverfiApiCtrl(EverfiRecordService everfiRecordService, PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                         EverfiUserService everfiUserService, EverfiCategoryService everfiCategoryService) {
        this.everfiRecordService = everfiRecordService;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.everfiUserService = everfiUserService;
        this.everfiCategoryService = everfiCategoryService;
    }

    /**
     * Everfi - Manual User Sync
     * ---------------------------------------
     * <p>
     * Manually trigger the processes that sync Employees with Everfi
     * <p>
     * <p>
     * Usage:
     * (POST)    /api/v1/everfi/manual/user/sync
     *
     * @return String
     *
     */
    @RequestMapping(value = "/manual/user/sync", method = {POST})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse manualUserSync(HttpServletRequest request,
                                         HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());
        everfiUserService.runUpdateMethods();
        return new SimpleResponse(true, "Everfi Manual User Sync", "everfi-manual-user-sync");
    }

    /**
     * Everfi - Cache Refresh
     * ---------------------------------------
     * <p>
     * ESS refreshes its everfi content id cache and its assignment id cache
     * <p>
     * This is necessary for handling a new task without restarting ESS
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/cache/refresh
     *
     * @return String
     *
     */
    @RequestMapping(value = "/cache/refresh", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse refreshEverfiCaches(HttpServletRequest request,
                                              HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());
        everfiRecordService.refreshCaches();
        return new SimpleResponse(true, "Everfi Caches Refreshed", "everfi-cache-refresh");
    }


    /**
     * Everfi - Personnel Employee Task Data Import
     * --------------------------------------------
     * <p>
     * ESS contacts Everfi for course data
     * <p>
     * Usage:
     * (POST)    /api/v1/everfi/personnel/task/generate
     * (POST)    /api/v1/everfi/personnel/task/generate?since=threeMonthsAgo
     * (POST)    /api/v1/everfi/personnel/task/generate?since=lastYear
     * (POST)    /api/v1/everfi/personnel/task/generate?since=1970
     *
     * @return String
     * @Param from, the beginning of the date range needed for the records
     * @Param Organization, is always senate
     *
     */
    @RequestMapping(value = "/personnel/task/generate", method = GET)
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runEverfiImport(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(required = false, defaultValue = "1970") String since) {
//        checkPermission(ADMIN.getPermission());

        LocalDateTime ldtsince;

        //Set From
        if (since.equals("1970")) {
            ldtsince = jan1970;
        } else if (since.equals("lastYear")) {
            ldtsince = lastYearJan;
        } else if (since.equals("threeMonthsAgo")) {
            ldtsince = threeMonthsAgo;
        } else {
            ldtsince = stringToLocalDateTime(since);
        }

        //Contact everfi
        try {
            logger.info(ldtsince.toString());
            everfiRecordService.contactEverfiForUserRecords(ldtsince.toString());
        } catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-report-generation");
        }

        return new SimpleResponse(true, "Everfi Report Generation", "everfi-report-generation");
    }


    /**
     * Everfi - User Records Import
     * ---------------------------------------
     * <p>
     * ESS contacts Everfi for user data and imports it into our database
     * <p>
     * Usage:
     * (POST)    /api/v1/everfi/import/users
     *
     * @return String
     *
     */
    @RequestMapping(value = "/import/users", method = POST)
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse importEverfiUserRecords(HttpServletRequest request, HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());

        try {
            everfiUserService.getEverfiUserIds();
        } catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-user-import");
        }

        return new SimpleResponse(true, "Everfi User Import", "everfi-user-import");
    }

    /**
     * Everfi - Get New Employees
     * ---------------------------------------
     * <p>
     * Returns a list of new employees that will need to be added to Everfi
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/new/emp
     *
     * @return String
     *
     */
    @RequestMapping(value = "/new/emp", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse getNewEmployees(HttpServletRequest request, HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());

        try {
            List<Employee> newEmployees = everfiUserService.getNewEmployeesToAddToEverfi();
            everfiUserService.addEmployeesToEverfi(newEmployees);
            return new SimpleResponse(true, "Number of new employees to be added to Everfi "
                    + newEmployees.size(), "everfi-new-employees");
        } catch (Exception e) {
            logger.info("Error adding new employees to everfi", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-user-import");
        }
    }

    /**
     * Everfi - Update Department Category
     * ---------------------------------------
     * <p>
     * ESS refreshes its everfi content id cache and its assignment id cache
     * <p>
     * This is necessary for ensuring departments exist for the new employees
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/department/update
     *
     * @return String
     */
    @RequestMapping(value = "/department/update", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse updateDepartmentCategeoryLabel(HttpServletRequest request,
                                                         HttpServletResponse response) throws IOException {
        checkPermission(ADMIN.getPermission());
        everfiCategoryService.ensureDepartmentIsUpToDate();
        return new SimpleResponse(true, "Everfi Department Category Updated",
                "everfi-department-update");
    }

    /**
     * Everfi - Update All Everfi Users
     * ---------------------------------------
     * <p>
     * Updates All users in Everfi with their current and most accurate info.
     * Maintains custom emails, will correct departments and NY Senate emails
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/users/all/update
     *
     * @return String
     */
    @RequestMapping(value = "/users/all/update", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse updateAllEverfiUsers(HttpServletRequest request,
                                               HttpServletResponse response) throws IOException {
        checkPermission(ADMIN.getPermission());
        everfiCategoryService.ensureDepartmentIsUpToDate();
        everfiUserService.updateAllEverfiUsers();
        return new SimpleResponse(true, "Everfi Department Category Updated",
                "everfi-department-update");
    }


    /**
     * Everfi - Active Status Change for Employee by Employee ID
     * -------------------------------------------
     * <p>
     * Chnage a users active status on Everfi by their employee ID
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/status/empid/{empid}/{status}
     *
     * @return String
     */
    @RequestMapping(value = "/status/empid/{empid}/{status}", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse changeStatusForEverfiUserWithEmpID(HttpServletRequest request,
                                                             HttpServletResponse response,
                                                             @PathVariable int empid,
                                                             @PathVariable boolean status) throws Exception {
        checkPermission(ADMIN.getPermission());
        everfiUserService.changeActiveStatusForUserWithEmpID(empid, status);
        return new SimpleResponse(true, "Everfi User Active Status Updated",
                "everfi-user-active-status-update");
    }

    /**
     * Everfi - Active Status Change for Employee by Everfi UUID
     * ----------------------------------------------------------
     * <p>
     * Change a users active status on Everfi by their Everfi UUID
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/status/uuid/{uuid}/{status}
     *
     * @return String
     */
    @RequestMapping(value = "/status/uuid/{uuid}/{status}", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse changeStatusForEverfiUserWithUUID(HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            @PathVariable String uuid,
                                                            @PathVariable boolean status) throws Exception {
        checkPermission(ADMIN.getPermission());
        everfiUserService.changeActiveStatusForUserWithUUID(uuid, status);
        return new SimpleResponse(true, "Everfi User Active Status Updated",
                "everfi-user-active-status-update");
    }

    /**
     * Everfi - Inactivate employees
     * ----------------------------------------------------------
     * <p>
     * Get recently inactivated employees in and update them in Everfi
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/inactivate/employees
     *
     * @return String
     */
    @RequestMapping(value = "/inactivate/employees", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse handleInactivatedEmployeesInEverfi(HttpServletRequest request,
                                                             HttpServletResponse response) throws Exception {
        checkPermission(ADMIN.getPermission());
        everfiUserService.handleInactivatedEmployeesInEverfi();
        return new SimpleResponse(true, "Updated inactive employees in Everfi",
                "updated-inactive-employees-in-everfi");
    }

    private LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }

}
