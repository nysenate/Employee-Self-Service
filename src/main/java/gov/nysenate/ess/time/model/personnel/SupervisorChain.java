package gov.nysenate.ess.time.model.personnel;

import java.util.LinkedHashSet;

/**
 * An employee has a supervisor hierarchy that is represented as a chain. The chain
 * is useful for determining who can be granted a supervisor override if the employee is a
 * supervisor.
 */
public class SupervisorChain
{
    protected int employeeId;
    private LinkedHashSet<Integer> chain = new LinkedHashSet<>();

    public SupervisorChain() {}

    public SupervisorChain(int empId) {
        this.employeeId = empId;
    }

    public void addSupervisorToChain(int nextSupId) {
        if (!chain.contains(nextSupId) && nextSupId != this.employeeId) {
            chain.add(nextSupId);
        }
    }

    public void addAlterations(SupervisorChainAlteration alteration) {
        if (alteration != null && alteration.hasAnyAlterations()) {
            chain.removeAll(alteration.chainExclusions);
            chain.addAll(alteration.chainInclusions);
        }
    }

    public boolean containsSupervisor(int supId) {
        return chain.contains(supId);
    }

    /** Basic Getters/Setters */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LinkedHashSet<Integer> getChain() {
        return chain;
    }
}
