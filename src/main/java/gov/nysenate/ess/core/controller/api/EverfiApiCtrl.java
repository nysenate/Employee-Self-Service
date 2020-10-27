package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/everfi/")
public class EverfiApiCtrl extends BaseRestApiCtrl {

    private EverfiRecordService everfiRecordService;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private EverfiUserService everfiUserService;
    private EverfiCategoryService everfiCategoryService;
    final LocalDateTime jan1970 = LocalDateTime.of(1970,1,1,0,0,0,0);
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
     * Everfi - Cache Refresh
     * ---------------------------------------
     *
     * ESS refreshes its everfi content id cache and its assignment id cache
     *
     * This is necessary for handling a new task without restarting ESS
     *
     * Usage:
     * (GET)    /api/v1/everfi/cache/refresh
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/cache/refresh", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse refreshEverfiCaches(HttpServletRequest request,
                                          HttpServletResponse response) {
        everfiRecordService.refreshCaches();
        return new SimpleResponse(true, "Everfi Caches Refreshed", "everfi-cache-refresh");
    }


    /**
     * Everfi - Personnel Employee Task Data Import
     * --------------------------------------------
     *
     * ESS contacts Everfi for course data
     *
     * Usage:
     * (GET)    /api/v1/everfi/personnel/task/generate
     *
     * @Param from, the beginning of the date range needed for the records
     *
     * @Param Organization, is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/personnel/task/generate", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runEverfiImport(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(required = false, defaultValue = "1970") String since) {
        checkPermission(ADMIN.getPermission());

        LocalDateTime ldtsince;

        //Set From
        if (since.equals("1970")) {
            ldtsince = jan1970;
        }
        else {
            ldtsince = stringToLocalDateTime(since);
        }

        //Contact everfi
        try {
            logger.debug(ldtsince.toString() + ":00.000");
            everfiRecordService.contactEverfiForUserRecords(ldtsince.toString() + ":00.000");
        }
        catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-report-generation");
        }

        return new SimpleResponse(true, "Everfi Report Generation", "everfi-report-generation");
    }


    /**
     * Everfi - User Records Import
     * ---------------------------------------
     *
     * ESS contacts Everfi for user data and imports it into our database
     *
     * Usage:
     * (GET)    /api/v1/everfi/import/users
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/import/users", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse importEverfiUserRecords(HttpServletRequest request, HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());
        
        try {
            everfiUserService.getEverfiUserIds();
        }
        catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-user-import");
        }

        return new SimpleResponse(true, "Everfi User Import", "everfi-user-import");
    }

    /**
     * Everfi - Get New Employees
     * ---------------------------------------
     *
     * Returns a list of new employees that will need to be added to Everfi
     *
     * Usage:
     * (GET)    /api/v1/everfi/new/emp
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/new/emp", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse getNewEmployees(HttpServletRequest request, HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());

        try {
            List<Employee> newEmployees = everfiUserService.getNewEmployeesToAddToEverfi();
            everfiUserService.addEmployeesToEverfi(newEmployees);
            return new SimpleResponse(true, "Number of new employees to be added to Everfi "
                    + newEmployees.size(), "everfi-new-employees");
        }
        catch (Exception e) {
            logger.info("Error adding new employees to everfi", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-user-import");
        }
    }

    /**
     * Everfi - Update Department Category
     * ---------------------------------------
     *
     * ESS refreshes its everfi content id cache and its assignment id cache
     *
     * This is necessary for ensuring departments exist for the new employees
     *
     * Usage:
     * (GET)    /api/v1/everfi/department/update
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/department/update", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse updateDepartmentCategeoryLabel(HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        everfiCategoryService.ensureDepartmentIsUpToDate();
        return new SimpleResponse(true, "Everfi Department Category Updated",
                "everfi-department-update");
    }


    private LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }

}
