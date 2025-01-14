import styles from "app/views/time/universalStyles.module.css";
import { formatDateStandard, timeRecordStatus } from "app/views/time/helpers";
import React from "react";


const ActiveAttendanceRecords = ({ state, linkToEntryPage, showDetails }) => {
  return (
    <div className={styles.contentContainer}>
      <h1>Active Attendance Records</h1>
      <p className={styles.contentInfo}>
        The following time records are in progress or awaiting submission.
        <br />
        {linkToEntryPage ? 'You can edit a record by clicking the row.' : 'Click a row to view the in-progress record.'}
      </p>
      <div className={styles.paddingX}>
        <table id="attendance-active-table" className={`${styles.essTable} ${styles.attendanceListingTable}`}>
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
          {state.records.employee.map((record) => (
            <tr
              key={record.timeRecordId}
              onClick={() => showDetails(record)}
              className={styles.eTimesheet}
            >
              <td>{formatDateStandard(record.beginDate)} - {formatDateStandard(record.endDate)}</td>
              <td>{record.payPeriod.payPeriodNum}</td>
              <td>
                <span dangerouslySetInnerHTML={{ __html: timeRecordStatus(record.recordStatus, true) }}></span>
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
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ActiveAttendanceRecords;