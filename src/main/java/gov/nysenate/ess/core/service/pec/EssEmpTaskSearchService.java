package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EssEmpTaskSearchService implements EmpTaskSearchService {

    private static final Logger logger = LoggerFactory.getLogger(EssEmpTaskSearchService.class);

    private static final LocalDate placeholderContSrvDate = LocalDate.of(1995, 1, 1);

    private final EmployeeInfoService employeeInfoService;
    private final PersonnelAssignedTaskDao patDao;

    public EssEmpTaskSearchService(EmployeeInfoService employeeInfoService, PersonnelAssignedTaskDao patDao) {
        this.employeeInfoService = employeeInfoService;
        this.patDao = patDao;
    }

    @Override
    public PaginatedList<EmployeeTaskSearchResult> searchForEmpTasks(EmpPATQuery query, LimitOffset limitOffset) {

        logger.info("Searching tasks");
        List<PersonnelAssignedTask> tasks = patDao.getTasks(query.getPatQuery());
        ImmutableListMultimap<Integer, PersonnelAssignedTask> empTaskMap =
                Multimaps.index(tasks, PersonnelAssignedTask::getEmpId);

        EmployeeSearchBuilder esb = query.getEmpQuery();

        logger.info("Getting emps");
        List<EmployeeTaskSearchResult> resultList = empTaskMap.asMap().entrySet().stream()
                .map(e -> new EmployeeTaskSearchResult(
                        employeeInfoService.getEmployee(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toList());

        logger.info("Filtering emps");
        resultList = resultList.stream()
                .filter(etsr -> empMatchesQuery(etsr, esb))
                .collect(Collectors.toList());

        logger.info("Limiting List");
        List<EmployeeTaskSearchResult> limitedResultList = LimitOffset.limitList(resultList, limitOffset);

        return new PaginatedList<>(resultList.size(), limitOffset, limitedResultList);
    }

    private boolean empMatchesQuery(EmployeeTaskSearchResult result, EmployeeSearchBuilder searchBuilder) {
        Employee employee = result.getEmployee();
        if (searchBuilder.getActive() != null &&
                !searchBuilder.getActive().equals(employee.isActive())) {
            return false;
        }
        if (searchBuilder.getRespCtrHeadCodes() != null && !searchBuilder.getRespCtrHeadCodes().isEmpty() &&
                !searchBuilder.getRespCtrHeadCodes().contains(employee.getRespCenterHeadCode())) {
            return false;
        }
        if (searchBuilder.getName() != null) {
            String resultSearchString = normalizeSearchString(
                    employee.getLastName() + " " + employee.getFirstName() + " " + employee.getInitial());
            String querySearchString = normalizeSearchString(searchBuilder.getName());
            if (!resultSearchString.contains(querySearchString)) {
                return false;
            }
        }
        if (searchBuilder.getContinuousServiceFrom() != null || searchBuilder.getContinuousServiceTo() != null) {
            LocalDate fromDate = Optional.ofNullable(searchBuilder.getContinuousServiceFrom())
                    .orElse(DateUtils.LONG_AGO);
            LocalDate toDate = Optional.ofNullable(searchBuilder.getContinuousServiceTo())
                    .orElse(DateUtils.THE_FUTURE);
            Range<LocalDate> dateRange = Range.closed(fromDate, toDate);
            LocalDate senateContServiceDate = Optional.ofNullable(employee.getSenateContServiceDate())
                    .orElse(placeholderContSrvDate);
            if (!dateRange.contains(senateContServiceDate)) {
                return false;
            }
        }
        return true;
    }



    private String normalizeSearchString(String searchString) {
        return searchString.replaceAll("[^a-zA-Z ]", "")
                .trim()
                .replaceAll(" +", " ")
                .toLowerCase();
    }

}
