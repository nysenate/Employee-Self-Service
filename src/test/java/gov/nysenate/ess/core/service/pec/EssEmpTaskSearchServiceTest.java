package gov.nysenate.ess.core.service.pec;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.model.cache.CacheWarmEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.Employee;
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
        PATQueryBuilder pqb = new PATQueryBuilder()
                .setTaskIds(Arrays.asList(
                        new PersonnelTaskId(PersonnelTaskType.MOODLE_COURSE, 1),
                        new PersonnelTaskId(PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT,4),
                        new PersonnelTaskId(PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT,1)
                ));
        EmpPATQuery query = new EmpPATQuery(esb, pqb);
        Stopwatch sw = Stopwatch.createStarted();
        PaginatedList<EmployeeTaskSearchResult> result = taskSearchService.searchForEmpTasks(query, LimitOffset.ALL);
        List<EmployeeTaskSearchResult> results = result.getResults();
        logger.info("{}, {} results", sw.stop(), results.size());
        Set<Integer> empids = results.stream()
                .map(EmployeeTaskSearchResult::getEmployee)
                .map(Employee::getEmployeeId)
                .collect(Collectors.toSet());
        List<PersonnelAssignedTask> tasks = results.stream()
                .map(EmployeeTaskSearchResult::getTasks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        logger.info("{} emps, {} tasks", empids.size(), tasks.size());
    }
}