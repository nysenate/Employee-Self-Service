// RecordSelectionContainer.js
// Show the record selection container if there are records
import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css";
import { formatDateStandard, timeRecordStatus } from "app/views/time/helpers";

// Needs:
//    -onChange implemented
// Issues:
//    -Period end should not be 0 => fixing dueFromNowStr calculation in record-entry-ctrl.js: getRecords()
export default function RecordSelectionContainer({ state, getRecordRangeDisplay }) {
  const getDisplayText = (record) => {
    let overallUpdateDate = new Date(state.records[state.iSelectedRecord].overallUpdateDate);
    let originalDate = new Date(state.records[state.iSelectedRecord].originalDate);
    if(record) {
      overallUpdateDate = new Date(record.overallUpdateDate);
      originalDate = new Date(record.originalDate);
    }
    const isSameSecond = overallUpdateDate.getTime() === originalDate.getTime();

    const displayText = isSameSecond
      ? 'New'
      : overallUpdateDate.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
      });
    return displayText;
  }


  return (
    <div className={`${styles.recordSelectionContainer} ${styles.contentContainer} ${styles.contentControls}`}>
      <p className={styles.contentInfo}>
        Enter a time and attendance record by selecting from the list of active pay periods.
      </p>
      {/* Show a simple table if there are 5 or fewer records */}
      {state.records.length <= 5 ? (
        // I have to add the styles below because simpleTable is overriding recordSelectionContainer
        // instead of the intended vice versa
        <table className={styles.simpleTable} style={{width: '90%', textAlign: 'center'}}>
          <thead>
          <tr>
            <th>Select</th>
            <th>Pay Period</th>
            <th>Supervisor</th>
            <th>Period End</th>
            <th>Status</th>
            <th>Last Updated</th>
          </tr>
          </thead>
          <tbody>
          {state.records.map((record, index) => (
            <tr key={index} onClick={() => state.iSelectedRecord = index}>
              <td>
                <input
                  type="radio"
                  name="recordSelect"
                  value={index}
                  checked={state.iSelectedRecord === index}
                  // onChange={() => state.iSelectedRecord = index}
                />
              </td>
              <td>{formatDateStandard(record.payPeriod.startDate)} - {formatDateStandard(record.payPeriod.endDate)}</td>
              <td>{record.supervisor.fullName}</td>
              <td className={record.isDue ? styles.darkRed : ''}>{record.dueFromNowStr}</td>
              {/*In ActiveAttendanceRecords below*/}
              <td>
                <span dangerouslySetInnerHTML={{ __html: timeRecordStatus(record.recordStatus, true) }}></span>
              </td>
              <td> {getDisplayText(record)}
              </td>
            </tr>
          ))}
          </tbody>
        </table>
      ) : (
         <div>
           <div className={styles.recordSelectionMenuDetails}>
             <label className={styles.bold}>Record Dates:
               <select
                 className={styles.recordSelectionMenu}
                 value={state.iSelectedRecord}
                 // onChange={(e) => state.iSelectedRecord = e.target.value}
               >
                 {state.records.map((record, index) => (
                   <option key={index} value={index}>{getRecordRangeDisplay(record)}</option>
                 ))}
               </select>
             </label>
             <span><span className={styles.bold}>Supervisor: </span>{state.records[state.iSelectedRecord].supervisor.fullName}</span>
             <span><span className={styles.bold} style={{ marginLeft: '10px'}}>Status: </span>{state.records[state.iSelectedRecord].recordStatus}</span>
             <span className={styles.bold} style={{ marginLeft: '10px'}}>Last Updated: </span>{getDisplayText(null)}
           </div>
         </div>
       )}
    </div>
  )
}