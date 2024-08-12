// RecordSelectionContainer.js
// Show the record selection container if there are records
import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css";


export default function RecordSelectionContainer({ state }) {

  return (
    <div className={`${styles.recordSelectionContainer} ${styles.contentContainer} ${styles.contentControls}`}>
      <p className={styles.contentInfo}>
        Enter a time and attendance record by selecting from the list of active pay periods.
      </p>
      {/* Show a simple table if there are 5 or fewer records */}
      {state.records.length <= 5 ? (
        <table className={styles.simpleTable}>
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
                  onChange={() => state.iSelectedRecord = index}
                />
              </td>
              <td>{`${moment(record.payPeriod.startDate).format('l')} - ${moment(record.payPeriod.endDate).format('l')}`}</td>
              <td>{record.supervisor.fullName}</td>
              <td className={record.isDue ? styles.darkRed : ''}>{record.dueFromNowStr}</td>
              {/*In ActiveAttendanceRecords below*/}
              <td dangerouslySetInnerHTML={{ __html: record.recordStatus }}></td>
              <td>
                {moment(record.overallUpdateDate).isSame(record.originalDate, 'second')
                 ? 'New'
                 : moment(record.overallUpdateDate).format('lll')}
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
                 onChange={(e) => state.iSelectedRecord = e.target.value}
               >
                 {state.records.map((record, index) => (
                   <option key={index} value={index}>{getRecordRangeDisplay(record)}</option>
                 ))}
               </select>
             </label>
             <span><span className={styles.bold}>Supervisor: </span>{state.records[state.iSelectedRecord].supervisor.fullName}</span>
             <span><span className={`${styles.marginLeft10} ${styles.bold}`}>Status: </span>{state.records[state.iSelectedRecord].recordStatus}</span>
             <span className={`${styles.marginLeft10} ${styles.bold}`}>Last Updated: </span>
             {moment(state.records[state.iSelectedRecord].overallUpdateDate).isSame(state.records[state.iSelectedRecord].originalDate, 'second')
              ? 'New'
              : moment(state.records[state.iSelectedRecord].overallUpdateDate).format('lll')}
           </div>
         </div>
       )}
    </div>
  )
}