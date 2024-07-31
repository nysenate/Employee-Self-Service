// RecordDetailsPopup.js
import React from 'react';
import Popup from "../../../components/Popup";
import styles from "../universalStyles.module.css";
import {
  formatDateToMMDDYYYY,
  timeRecordStatus,
  checkEntryTypes,
  entryHoursFilter,
  miscLeave
} from "app/views/time/helpers";


export function RecordDetailsPopup({ record, isModalOpen, closeModal }) {
  const { tempEntries, annualEntries } = checkEntryTypes(record.timeEntries);

  const Title = ({ record }) => {
    return `Attendance record for ${record.employee.fullName} 
    from ${formatDateToMMDDYYYY(record.beginDate)} 
    to ${formatDateToMMDDYYYY(record.endDate)}`;
  };

  return (
    <Popup
      isLocked={false}
      isOpen={isModalOpen}
      onClose={closeModal}
      title={Title({ record })}
    >
      <div className={styles.grid}>
        {/* Right Table */}
        <div className={styles.col1012}>
          {tempEntries && (
            <div className={styles.temp}>
              {annualEntries && (
                <h1 className={styles.attendanceEntrySubTableTitle} style={{ margin: '10px' }}>
                  Temporary Pay Entries
                </h1>
              )}
              <table className={`${styles.attendanceEntrySubTable} ${styles.essTable}`}>
                <thead>
                <tr>
                  <th className={styles.dayCol}>Day</th>
                  <th className={styles.dateCol}>Date</th>
                  <th className={styles.hourCol}>Work</th>
                  <th>Work Time Description / Comments</th>
                </tr>
                </thead>
                <tbody>
                {record.timeEntries.map((entry, index) => (
                  <tr key={index}>
                    <td>{entry.date}</td>
                    <td>{entry.date}</td>
                    <td>{entryHoursFilter(entry.workHours)}</td>
                    <td className="entry-comment">{entry.empComment}</td>
                  </tr>
                ))}
                {!annualEntries && (
                  <tr className={styles.timeTotalsRow}>
                    <td></td>
                    <td><strong>Record Totals</strong></td>
                    <td><strong>{record.totals.tempWorkHours}</strong></td>
                    <td></td>
                  </tr>
                )}
                </tbody>
              </table>
            </div>
          )}
          {annualEntries && (
            <div className={styles.raSa}>
              {tempEntries && (
                <h1 className={styles.attendanceEntrySubTableTitle} style={{ margin: '10px' }}>
                  Regular/Special Annual Pay Entries
                </h1>
              )}
              <table className={`${styles.attendanceEntrySubTable} ${styles.essTable}`}>
                <thead>
                <tr>
                  <th className={styles.dayCol}>Day</th>
                  <th className={styles.dateCol}>Date</th>
                  <th className={styles.hourCol}>Work</th>
                  <th className={styles.hourCol}>Holiday</th>
                  <th className={styles.hourCol}>Vacation</th>
                  <th className={styles.hourCol}>Personal</th>
                  <th className={styles.hourCol}>Sick Emp</th>
                  <th className={styles.hourCol}>Sick Fam</th>
                  <th className={styles.hourCol}>Misc</th>
                  <th>Misc Type</th>
                  <th className={styles.hourCol}>Total</th>
                </tr>
                </thead>
                <tbody>
                {record.timeEntries.map((entry, index) => (
                  <tr key={index}>
                    <td>{entry.date}</td>
                    <td>{entry.date}</td>
                    <td>{entryHoursFilter(entry.workHours)}</td>
                    <td>{entryHoursFilter(entry.holidayHours)}</td>
                    <td>{entryHoursFilter(entry.vacationHours)}</td>
                    <td>{entryHoursFilter(entry.personalHours)}</td>
                    <td>{entryHoursFilter(entry.sickEmpHours)}</td>
                    <td>{entryHoursFilter(entry.sickFamHours)}</td>
                    <td>{entryHoursFilter(entry.miscHours)}</td>
                    {/* miscType needs a filter miscLeave()  */}
                    <td>{miscLeave(entry.miscType)}</td>
                    <td>{entry.total}</td>
                  </tr>
                ))}
                <tr className={styles.timeTotalsRow}>
                  <td></td>
                  <td><strong>Record Totals</strong></td>
                  <td><strong>{record.totals.workHours}</strong></td>
                  <td><strong>{record.totals.holidayHours}</strong></td>
                  <td><strong>{record.totals.vacationHours}</strong></td>
                  <td><strong>{record.totals.personalHours}</strong></td>
                  <td><strong>{record.totals.sickEmpHours}</strong></td>
                  <td><strong>{record.totals.sickFamHours}</strong></td>
                  <td><strong>{record.totals.miscHours}</strong></td>
                  <td></td>
                  <td><strong>{record.totals.total}</strong></td>
                </tr>
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* LEFT */}
        <div className={styles.col212}>
          <h0 className={styles.contentInfo}>Notes</h0>
          <div className={styles.recordDetailsSection}>
            {!record.remarks ? (
              <label>This time record has no notes</label>
            ) : (
               <span>{record.remarks}</span>
             )}
          </div>

          <h0 className={styles.contentInfo}>Supervisor</h0>
          <div className={styles.recordDetailsSection}>
            <span>{record.supervisor.fullName}</span>
          </div>

          <h0 className={styles.contentInfo}>Status</h0>
          <div className={styles.recordDetailsSection}>
            <span dangerouslySetInnerHTML={{ __html: timeRecordStatus(record.recordStatus) }}></span>
          </div>

          <h0 className={styles.contentInfo}>Actions</h0>
          <div className={styles.recordDetailsSection}>
            <a href="#print" tabIndex="0">Print Record</a>
            <div>
              <br />
              <br />
              <a href="#close" onClick={closeModal} tabIndex="0">Exit</a>
            </div>
          </div>
        </div>
      </div>
    </Popup>
  );
}


// essTime.filter("miscLeave", [ "appProps", function(appProps) {
//     var miscLeaveMap = {};
//     return angular.forEach(appProps.miscLeaves, function(miscLeave) {
//         miscLeaveMap[miscLeave.type] = miscLeave;
//     }), function(miscLeave, defaultLabel) {
//         return miscLeaveMap.hasOwnProperty(miscLeave) ? miscLeaveMap[miscLeave].shortName : miscLeave ? miscLeave + "?!" : defaultLabel || "--";
//     };
// } ]),
// angular.module("essTime").filter("entryHours", [ entryHoursFilter ]).directive("timeRecordInput", [ timeRecordInputDirective ]).directive("recordDetails", [ "appProps", "modals", "AccrualPeriodApi", "AllowanceApi", "AllowanceUtils", recordDetailsDirective ]).directive("recordDetailModal", [ "modals", recordDetailModalDirective ]),