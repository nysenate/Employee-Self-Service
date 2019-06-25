package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

import java.util.Comparator;

public enum EmpTaskOrderBy {

    NAME(
            Comparator.comparing(
                    EmployeeTaskSearchResult::getEmployee,
                    Comparator.comparing(e -> e.getLastName() + e.getFirstName() + e.getInitial())
            )
    ),

    OFFICE(
            Comparator.comparing(
                    r -> r.getEmployee().getRespCenter().getHead().getName()
            )
    ),

    COMPLETED(
            Comparator.comparing(
                    r -> r.getTasks().stream()
                            .filter(PersonnelAssignedTask::isCompleted)
                            .count()
            )
    ),
    ;

    private Comparator<EmployeeTaskSearchResult> comparator;

    EmpTaskOrderBy(Comparator<EmployeeTaskSearchResult> comparator) {
        this.comparator = comparator;
    }

    public Comparator<EmployeeTaskSearchResult> getComparator() {
        return comparator;
    }
}
