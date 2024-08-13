// AnnualEntryForm.js
import styles from "../universalStyles.module.css";

export default function AnnualEntryForm({ state, accrualsLoading }) {

  return(
    <>
      {state.annualEntries && (
        <div className={styles.raSaEntry}>
          {state.tempEntries && <h1 className={styles.timeEntryTableTitle}>Regular/Special Annual Pay Entries</h1>}
          <AccrualBar accruals={state.accrual} loading={accrualsLoading} />
          <hr className={styles.marginTop10} />
          {selRecordHasRaSaErrors() && (
            <div className={`${styles.essNotification} ${styles.timeEntryErrorBox} ${styles.marginTop20}`} level="error" title="Time record has errors">
              <ul>
                {errorTypes.raSa.workHoursInvalidRange && <li>Work hours must be between 0 and 24.</li>}
                {errorTypes.raSa.holidayHoursInvalidRange && <li>Holiday hours must be at least 0 and may not exceed hours granted for the holiday</li>}
                {errorTypes.raSa.vacationHoursInvalidRange && <li>Vacation hours must be between 0 and 12.</li>}
                {errorTypes.raSa.personalHoursInvalidRange && <li>Personal hours must be between 0 and 12.</li>}
                {errorTypes.raSa.sickEmpHoursInvalidRange && <li>Employee sick hours must be between 0 and 12.</li>}
                {errorTypes.raSa.sickFamHoursInvalidRange && <li>Family sick hours must be between 0 and 12.</li>}
                {errorTypes.raSa.miscHoursInvalidRange && <li>Misc hours must be between 0 and 12.</li>}
                {errorTypes.raSa.totalHoursInvalidRange && <li>Total hours must be between 0 and 24.</li>}
                {errorTypes.raSa.notEnoughVacationTime && <li>Vacation hours recorded exceeds hours available.</li>}
                {errorTypes.raSa.notEnoughPersonalTime && <li>Personal hours recorded exceeds hours available.</li>}
                {errorTypes.raSa.notEnoughSickTime && <li>Sick hours recorded exceeds hours available.</li>}
                {errorTypes.raSa.noMiscTypeGiven && <li>A Misc type must be given when using Miscellaneous hours.</li>}
                {errorTypes.raSa.noMiscHoursGiven && <li>Miscellaneous hours must be present when a Misc type is selected.</li>}
                {errorTypes.raSa.halfHourIncrements && <li>Hours must be in increments of 0.5</li>}
                {errorTypes.raSa.notEnoughMiscTime && state.miscLeaveUsageErrors.map((data, index) => (
                  <li key={index}>
                    Your total of {data.hoursUsed} {data.shortname} hours exceeds the limit of {data.hoursRemaining} for the period {data.range}
                  </li>
                ))}
              </ul>
            </div>
          )}
          <table className={`${styles.essTable} ${styles.timeRecordEntryTable}`} id="ra-sa-time-record-table" ng-model="state.records[state.iSelectedRecord].timeEntries">
            <thead>
            <tr>
              <th>Date</th>
              <th>Work</th>
              <th>Holiday</th>
              <th>Vacation</th>
              <th>Personal</th>
              <th>Sick Emp</th>
              <th>Sick Fam</th>
              <th>Misc</th>
              <th>Misc Type</th>
              <th>Total</th>
            </tr>
            </thead>
            <tbody record-validator validate={preValidation} record={state.records[state.iSelectedRecord]}>
            {state.records[state.iSelectedRecord].timeEntries.filter(entry => entry.payType !== 'TE').map((entry, i) => (
              <tr key={i} className={`${styles.timeRecordRow} ${styles.highlightFirst}`} ng-class={{ 'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry }}>
                <td className={styles.dateColumn}>{moment(entry.date).format('ddd M/D/YYYY')}</td>
                <td entry-validator validate={entryValidators.raSa.workHours(entry)}>
                  <input
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="24"
                    disabled={moment(entry.date).isAfter(moment(), 'day')}
                    value={entry.workHours}
                    tabIndex={entry.total < 7 || getSelectedRecord().focused ? 1 : -1}
                    name="numWorkHours"
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.holidayHours(entry)}>
                  <input
                    id={`${entry.date}-holidayHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    readOnly={!isHoliday(entry)}
                    placeholder={isHoliday(entry) ? '--' : ''}
                    step="0.5"
                    min="0"
                    max={getHolidayHours(entry)}
                    value={entry.holidayHours}
                    name="numHolidayHours"
                    tabIndex={isHoliday(entry) ? accrualTabIndex.holiday(entry) : -1}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.vacationHours(entry)}>
                  <input
                    id={`${entry.date}-vacationHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="12"
                    value={entry.vacationHours}
                    name="numVacationHours"
                    tabIndex={accrualTabIndex.vacation(entry)}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.personalHours(entry)}>
                  <input
                    id={`${entry.date}-personalHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="12"
                    value={entry.personalHours}
                    name="numPersonalHours"
                    tabIndex={accrualTabIndex.personal(entry)}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.sickEmpHours(entry)}>
                  <input
                    id={`${entry.date}-sickEmpHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="12"
                    value={entry.sickEmpHours}
                    name="numSickEmpHours"
                    tabIndex={accrualTabIndex.sickEmp(entry)}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.sickFamHours(entry)}>
                  <input
                    id={`${entry.date}-sickFamHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="12"
                    value={entry.sickFamHours}
                    name="numSickFamHours"
                    tabIndex={accrualTabIndex.sickFam(entry)}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.miscHours(entry)}>
                  <input
                    id={`${entry.date}-miscHours`}
                    type="number"
                    onChange={() => setDirty(entry, i)}
                    className={styles.hoursInput}
                    placeholder="--"
                    step="0.5"
                    min="0"
                    max="12"
                    value={entry.miscHours}
                    name="numMiscHours"
                    tabIndex={accrualTabIndex.misc(entry)}
                  />
                </td>
                <td entry-validator validate={entryValidators.raSa.miscType(entry)}>
                  <select
                    id={`${entry.date}-miscType`}
                    style={{ fontSize: '.9em' }}
                    name="miscHourType"
                    value={entry.miscType}
                    onChange={() => setDirty(entry, i)}
                    tabIndex={entry.miscHours ? 1 : -1}
                  >
                    <option value="">No Misc Hours</option>
                    {state.miscLeaves.filter(getMiscLeavePredicate(entry.date)).map((miscLeave, index) => (
                      <option key={index} value={miscLeave.type}>{miscLeave.shortName}</option>
                    ))}
                  </select>
                </td>
                <td className={styles.textAlignCenter} entry-validator validate={entryValidators.raSa.totalHours(entry)}>
                  <span>{entry.total.toFixed(2)}</span>
                </td>
              </tr>
            ))}
            <tr className={styles.timeTotalsRow}>
              <td><span ng-if={state.tempEntries}>RA/SA</span> Record Totals</td>
              <td>{state.totals.raSaWorkHours}</td>
              <td>{state.totals.holidayHours}</td>
              <td>{state.totals.vacationHours}</td>
              <td>{state.totals.personalHours}</td>
              <td>{state.totals.sickEmpHours}</td>
              <td>{state.totals.sickFamHours}</td>
              <td>{state.totals.miscHours}</td>
              <td></td>
              <td>{state.totals.raSaTotal}</td>
            </tr>
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}