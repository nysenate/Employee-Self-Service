package gov.nysenate.ess.core.client.view.alert;

import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SendWordNowCsv {

    public void createCsv(HttpServletResponse response, Set<Employee> employees, Map<Integer, AlertInfo> alertInfos) {
        try (CSVPrinter printer = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT)) {
            // Print Headers
            printer.printRecord("Source Key", "FirstName", "LastName", "Language", "Title", "Department", "LocationID", "AgencyCode",
                    "Address1", "Address2", "City", "State", "PostalCode", "Country", "TimeZone",
                    "Work Phone", "Work Email", "Home Phone", "Alternate Phone", "Alternate SMS", "Mobile Phone",
                    "Mobile SMS", "Personal Email", "Alternate Email");

            // Print a row for each employee
            for (Employee emp : employees) {
                if (isEmployeeInitialized(emp)) {
                    AlertInfo empAlertInfo = alertInfos.get(emp.getEmployeeId()) == null
                            ? AlertInfo.builder().build()
                            : alertInfos.get(emp.getEmployeeId());

                    printer.printRecord(
                            emp.getUid(),
                            emp.getFirstName(),
                            emp.getLastName(),
                            "en-US",
                            emp.getJobTitle(),
                            emp.getRespCenter().getHead().getName(),
                            emp.getWorkLocation().getLocId().getCode(),
                            emp.getRespCenter().getAgency().getCode(),
                            emp.getWorkLocation().getAddress().getAddr1(),
                            emp.getWorkLocation().getAddress().getAddr2(),
                            emp.getWorkLocation().getAddress().getCity(),
                            emp.getWorkLocation().getAddress().getState(),
                            emp.getWorkLocation().getAddress().getZip5(),
                            "United States",
                            "US/Eastern",
                            emp.getWorkPhone() == null ? "" : emp.getWorkPhone(),
                            emp.getEmail() == null ? "" : emp.getEmail(),
                            empAlertInfo.getHomePhone(),
                            empAlertInfo.getAlternateOptions().isCallable() ? empAlertInfo.getAlternatePhone() : "",
                            empAlertInfo.alternateSms(),
                            empAlertInfo.getMobileOptions().isCallable() ? empAlertInfo.getMobilePhone() : "",
                            empAlertInfo.mobileSms(),
                            empAlertInfo.getPersonalEmail(),
                            empAlertInfo.getAlternateEmail()
                            );
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Don't include employees in the dump if their information is incomplete in our system.
     * This should only include new employees while their info is being populated.
     *
     * @return true if this employee has been completely entered into our system, false otherwise.
     * An initialized employee should have a Uid, name, job title, resp center, resp center head,
     * agency, and work address.
     */
    private static boolean isEmployeeInitialized(Employee emp) {
        return emp.getUid() != null
                && emp.getFirstName() != null
                && emp.getLastName() != null
                && emp.getJobTitle() != null
                && emp.getRespCenter() != null
                && emp.getRespCenter().getHead() != null
                && emp.getRespCenter().getAgency() != null
                && emp.getWorkLocation() != null
                && emp.getWorkLocation().getAddress() != null;
    }
}
