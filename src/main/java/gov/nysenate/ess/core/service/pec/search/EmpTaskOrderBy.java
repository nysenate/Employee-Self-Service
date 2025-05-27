package gov.nysenate.ess.core.service.pec.search;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;

import java.util.Comparator;
import java.util.Optional;

public enum EmpTaskOrderBy {

    NAME(
            Comparator.comparing(
                    EmployeeTaskSearchResult::getEmployee,
                    Comparator.comparing(e -> e.getLastName() + e.getFirstName() + e.getInitial())
            )
    ),

    OFFICE(
            Comparator.comparing(
                    r -> Optional.ofNullable(r.getEmployee())
                            .map(Employee::getRespCenter)
                            .map(ResponsibilityCenter::getHead)
                            .map(ResponsibilityHead::getName)
                            .orElse(null),
                    Comparator.nullsFirst(String::compareToIgnoreCase)
            )
    ),

    COMPLETED(
            Comparator.comparing(
                    r -> r.getTasks().stream()
                            .filter(PersonnelTaskAssignment::isCompleted)
                            .count()
            )
    ),
    ;

    private final Comparator<EmployeeTaskSearchResult> comparator;

    EmpTaskOrderBy(Comparator<EmployeeTaskSearchResult> comparator) {
        this.comparator = comparator;
    }

    public Comparator<EmployeeTaskSearchResult> getComparator() {
        return comparator;
    }
}
