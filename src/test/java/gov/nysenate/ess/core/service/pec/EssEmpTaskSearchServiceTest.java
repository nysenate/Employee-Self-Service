package gov.nysenate.ess.core.service.pec;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.model.cache.CacheWarmEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.search.*;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.util.SortOrder.ASC;
import static gov.nysenate.ess.core.util.SortOrder.DESC;

@Category(SillyTest.class)
public class EssEmpTaskSearchServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssEmpTaskSearchServiceTest.class);

    @Autowired private EmpTaskSearchService taskSearchService;
    @Autowired private EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.post(new CacheWarmEvent(Collections.singleton(ContentCache.EMPLOYEE)));
    }

    @Test
    public void speedTest() {
        EmployeeSearchBuilder esb = new EmployeeSearchBuilder();
        PTAQueryBuilder pqb = new PTAQueryBuilder();
        EmpTaskOrderBy orderBy = EmpTaskOrderBy.NAME;
        List<EmpTaskSort> sortDirectives = Arrays.asList(
                new EmpTaskSort(EmpTaskOrderBy.COMPLETED, DESC),
//                new EmpTaskSort(EmpTaskOrderBy.OFFICE, DESC),
                new EmpTaskSort(EmpTaskOrderBy.NAME, ASC)
        );
        EmpPTAQuery query = new EmpPTAQuery(esb, pqb, sortDirectives);
        Stopwatch sw = Stopwatch.createStarted();
        PaginatedList<EmployeeTaskSearchResult> result = taskSearchService.searchForEmpTasks(query, LimitOffset.ALL);
        List<EmployeeTaskSearchResult> results = result.getResults();
        logger.info("{}, {} results", sw.stop(), results.size());
        Set<Integer> empids = results.stream()
                .map(EmployeeTaskSearchResult::getEmployee)
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        List<PersonnelTaskAssignment> tasks = results.stream()
                .map(EmployeeTaskSearchResult::getTasks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        logger.info("{} emps, {} tasks", empids.size(), tasks.size());
    }
}