import React from "react";
import { format, parseISO } from "date-fns";
import styles from "app/views/time/accrual/accrual.module.css";
import { HourCell } from "app/views/time/accrual/AccrualHistoryTable";
import {
  isPerValid,
  isSickDonationValid,
  isSickEmpValid,
  isSickFamValid,
  isVacValid,
} from "app/views/time/accrual/projectionUtils";

/**
 * The projected accrual table of the Accrual Projections page.
 * Ported from WEB-INF/view/template/time/accrual/projections-directive.jsp.
 *
 * @param projections The projections, in chronological order.
 * @param onUsageChange Called with (index, field, type, hours) when a usage field is edited.
 * @param onRecordClick Called with a record when a non input cell of its row is clicked.
 */
export default function AccrualProjectionsTable({
  projections,
  onUsageChange,
  onRecordClick,
}) {
  return (
    <table className={styles.accrualTable}>
      <thead>
        <tr>
          <th colSpan={3}>Pay Period</th>
          <th colSpan={2}>Personal Hours</th>
          <th colSpan={3}>Vacation Hours</th>
          <th colSpan={4}>Sick Hours</th>
        </tr>
        <tr>
          <th>#</th>
          <th>Start Date</th>
          <th>End Date</th>
          <th className={styles.personal}>Use</th>
          <th className={styles.personal}>Avail</th>
          <th className={styles.vacation}>Rate</th>
          <th className={styles.vacation}>Use</th>
          <th className={styles.vacation}>Avail</th>
          <th className={styles.sick}>Rate</th>
          <th className={styles.sick}>Emp Use</th>
          <th className={styles.sick}>Fam Use</th>
          <th className={styles.sick}>Donated</th>
          <th className={styles.sick}>Avail</th>
        </tr>
      </thead>
      <tbody>
        {projections.map((record, index) => (
          <ProjectionRow
            key={record.payPeriod?.startDate}
            record={record}
            index={index}
            onUsageChange={onUsageChange}
            onRecordClick={onRecordClick}
          />
        ))}
      </tbody>
    </table>
  );
}

function ProjectionRow({ record, index, onUsageChange, onRecordClick }) {
  const rowClasses = [];
  if (record.payPeriod?.current) {
    rowClasses.push(styles.highlighted);
  }
  if (!record.valid) {
    rowClasses.push(styles.invalid);
  }

  // Only a valid record has a meaningful breakdown to show.
  const openDetails = () => record.valid && onRecordClick(record);

  const usageInput = (field, type, isValid) => (
    <UsageInput
      value={record[field]}
      max={record.maxHours}
      isValid={isValid(record)}
      onChange={(hours) => onUsageChange(index, field, type, hours)}
    />
  );

  return (
    <tr
      className={rowClasses.join(" ")}
      title={record.valid ? "Open a Detail View of this Record" : ""}
    >
      <td onClick={openDetails}>{record.payPeriod?.payPeriodNum}</td>
      <td onClick={openDetails}>{formatDate(record.payPeriod?.startDate)}</td>
      <td onClick={openDetails}>{formatDate(record.payPeriod?.endDate)}</td>

      <HourCell type="personal">
        {usageInput("biweekPersonalUsed", "personal", isPerValid)}
      </HourCell>
      <AvailableCell
        type="personal"
        changed={record.changed?.personal}
        isValid={record.validation?.per}
        hours={record.personalAvailable}
        onClick={openDetails}
      />

      <HourCell type="vacation">{record.vacationRate}</HourCell>
      <HourCell type="vacation">
        {usageInput("biweekVacationUsed", "vacation", isVacValid)}
      </HourCell>
      <AvailableCell
        type="vacation"
        changed={record.changed?.vacation}
        isValid={record.validation?.vac}
        hours={record.vacationAvailable}
        onClick={openDetails}
      />

      <HourCell type="sick">{record.sickRate}</HourCell>
      <HourCell type="sick">
        {usageInput("biweekSickEmpUsed", "sick", isSickEmpValid)}
      </HourCell>
      <HourCell type="sick">
        {usageInput("biweekSickFamUsed", "sick", isSickFamValid)}
      </HourCell>
      <HourCell type="sick">
        {usageInput("biweekSickDonated", "sick", isSickDonationValid)}
      </HourCell>
      <AvailableCell
        type="sick"
        changed={record.changed?.sick}
        isValid={record.validation?.sick}
        hours={record.sickAvailable}
        onClick={openDetails}
      />
    </tr>
  );
}

/** Available hours read "--" once something upstream of them has gone invalid. */
function AvailableCell({ type, changed, isValid, hours, onClick }) {
  return (
    <td
      className={[
        styles.accrualHours,
        styles.availableHours,
        styles[type],
        changed ? styles.changed : "",
      ].join(" ")}
      onClick={onClick}
    >
      {isValid ? hours : "--"}
    </td>
  );
}

function UsageInput({ value, max, isValid, onChange }) {
  return (
    <input
      type="number"
      className={isValid ? undefined : styles.invalid}
      min={0}
      max={max}
      step={0.5}
      placeholder="0"
      value={value === null || value === undefined ? "" : value}
      onChange={(e) => onChange(parseHours(e.target.value))}
    />
  );
}

function parseHours(inputValue) {
  if (inputValue === "") {
    return null;
  }
  const hours = parseFloat(inputValue);
  return isNaN(hours) ? null : hours;
}

function formatDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd/yyyy") : "";
}
