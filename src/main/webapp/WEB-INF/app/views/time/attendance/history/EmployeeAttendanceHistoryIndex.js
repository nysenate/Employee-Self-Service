import React from "react";
import EmployeeSelectPage from "app/views/time/personnel/EmployeeSelectPage";
import { AttendanceHistorySection } from "app/views/time/attendance/history/AttendanceHistoryIndex";

/**
 * Attendance history for any employee the user supervises.
 * Ported from WEB-INF/view/template/time/record/emp-history.jsp.
 */
export default function EmployeeAttendanceHistoryIndex() {
  return (
    <EmployeeSelectPage
      heading="Employee Attendance History"
      subject="Attendance Records"
    >
      {(empId) => (
        <AttendanceHistorySection empId={empId} linkActiveToEntry={false} />
      )}
    </EmployeeSelectPage>
  );
}
