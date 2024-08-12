// TemporaryEntryForm.js
import styles from "../universalStyles.module.css";

export default function TemporaryEntryForm() {

  return(
    <>
      {state.tempEntries && (
        <div className={styles.teEntry}>
          {state.annualEntries && <h1 className={styles.timeEntryTableTitle}>Temporary Pay Entries</h1>}
          <AllowanceBar allowance={state.allowances[state.selectedYear]} tempWorkHours={state.totals.tempWorkHours} loading={state.request.allowances} />
          <hr />
          {selRecordHasTeErrors() && (
            <div className={`${styles.essNotification} ${styles.timeEntryErrorBox} ${styles.marginTop20}`} level="error" title="Time record has errors">
              <ul>
                {errorTypes.te.workHoursInvalidRange && <li>Work hours must be between 0 and 24</li>}
                {errorTypes.te.notEnoughWorkHours && <li>Work hours recorded exceeds available work hours</li>}
                {errorTypes.te.fifteenMinIncrements && <li>Work hours must be in increments of 0.25</li>}
                {errorTypes.te.noComment && <li>Must enter start and end work times for all work blocks during the entered work hours.</li>}
                {errorTypes.te.noWorkHoursForComment && <li>Commented entries must accompany 0 or more work hours entered</li>}
              </ul>
            </div>
          )}
          <table className={`${styles.essTable} ${styles.timeRecordEntryTable}`} id="te-time-record-table">
            <thead>
            <tr>
              <th>Date</th>
              <th>Work</th>
              <th>Work Time Description / Comments</th>
            </tr>
            </thead>
            <tbody record-validator validate={preValidation} record={state.records[state.iSelectedRecord]}>
            {state.records[state.iSelectedRecord].timeEntries.filter(entry => entry.payType === 'TE').map((entry, i) => (
              <tr key={i} className={`${styles.timeRecordRow} ${styles.highlightFirst}`} ng-class={{ 'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry }}>
                <td className={styles.dateColumn}>{moment(entry.date).format('ddd M/D/YYYY')}</td>
                <td entry-validator validate={entryValidators.te.workHours(entry)}>
                  <input
                    type="number"
                    onChange={() => setDirty(entry)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.25"
                    min="0"
                    max="24"
                    disabled={moment(entry.date).isAfter(moment(), 'day')}
                    value={entry.workHours}
                    name="numWorkHours"
                    tabIndex="1"
                  />
                </td>
                <td entry-validator validate={entryValidators.te.comment(entry)} className={styles.entryCommentCol}>
                          <textarea
                            maxLength="150"
                            onChange={() => setDirty(entry)}
                            className={styles.entryComment}
                            value={entry.empComment}
                            name="entryComment"
                            tabIndex={entry.workHours ? 1 : -1}
                          />
                </td>
              </tr>
            ))}
            <tr className={styles.timeTotalsRow}>
              <td><span ng-if={state.annualEntries}>TE</span> Record Totals</td>
              <td>{state.totals.tempWorkHours}</td>
              <td></td>
            </tr>
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}