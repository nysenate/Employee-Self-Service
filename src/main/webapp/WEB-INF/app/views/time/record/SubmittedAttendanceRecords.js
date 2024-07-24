import styles from "app/views/time/universalStyles.module.css";
import { formatDateToMMDDYYYY } from "app/views/time/helpers";
import React from "react";


const SubmittedAttendanceRecords = ({ state, showDetails }) => {
  return (
    <div className={styles.contentContainer}>
      <h1>Submitted Attendance Records</h1>
      <p className={styles.contentInfo}>
        Time records that have been submitted for pay periods during {state.selectedRecYear} are listed in the table below.
        <br />
        You can view details about each pay period by clicking the row.
        {state.paperTimesheetsDisplayed && (
          <>
            <br />
            <span className={styles.bold}>Note:</span> Details are unavailable for attendance records entered via paper timesheet (designated by "(paper)" under Status)
          </>
        )}
      </p>
      <div className={styles.paddingX}>
        <table id="attendance-history-table" className={`${styles.essTable} ${styles.attendanceListingTable}`}>
          <thead>
          <tr>
            <th>Date Range</th>
            <th>Pay Period</th>
            <th>Status</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick Emp</th>
            <th>Sick Fam</th>
            <th>Misc</th>
            <th>Total</th>
          </tr>
          </thead>
          <tbody>
          {state.records.submitted.map((record) => (
            <tr
              key={record.timeRecordId}
              onClick={() => showDetails(record)}
              className={record.paperTimesheet ? '' : styles.eTimesheet}
            >
              <td>{formatDateToMMDDYYYY(record.beginDate)} - {formatDateToMMDDYYYY(record.endDate)}</td>
              <td>{record.payPeriod.payPeriodNum}</td>
              <td>
                <span dangerouslySetInnerHTML={{ __html: record.recordStatus }}></span>
                {record.paperTimesheet && <span>(paper)</span>}
              </td>
              <td>{record.totals.workHours}</td>
              <td>{record.totals.holidayHours}</td>
              <td>{record.totals.vacationHours}</td>
              <td>{record.totals.personalHours}</td>
              <td>{record.totals.sickEmpHours}</td>
              <td>{record.totals.sickFamHours}</td>
              <td>{record.totals.miscHours}</td>
              <td>{record.totals.total}</td>
            </tr>
          ))}
          <tr style={{ borderTop: '2px solid teal' }}>
            <td colSpan="2"></td>
            <td><strong>Annual Totals</strong></td>
            <td><strong>{state.annualTotals.workHours}</strong></td>
            <td><strong>{state.annualTotals.holidayHours}</strong></td>
            <td><strong>{state.annualTotals.vacationHours}</strong></td>
            <td><strong>{state.annualTotals.personalHours}</strong></td>
            <td><strong>{state.annualTotals.sickEmpHours}</strong></td>
            <td><strong>{state.annualTotals.sickFamHours}</strong></td>
            <td><strong>{state.annualTotals.miscHours}</strong></td>
            <td><strong>{state.annualTotals.total}</strong></td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default SubmittedAttendanceRecords;