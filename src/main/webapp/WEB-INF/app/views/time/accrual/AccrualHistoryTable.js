import React from "react";
import { format, parseISO } from "date-fns";
import styles from "app/views/time/accrual/accrual.module.css";

/**
 * The accrual summary table of the Accrual History page.
 * Ported from WEB-INF/view/template/time/accrual/history-directive.jsp.
 *
 * @param records Accrual summaries, in reverse chronological order.
 * @param onRecordClick Called with a record when its row is clicked.
 */
export default function AccrualHistoryTable({ records, onRecordClick }) {
  return (
    <table className={styles.accrualTable}>
      <thead>
        <tr>
          <th colSpan={2}>Pay Period</th>
          <th colSpan={4}>Personal Hours</th>
          <th colSpan={5}>Vacation Hours</th>
          <th colSpan={5}>Sick Hours</th>
        </tr>
        <tr>
          <th>#</th>
          <th>End Date</th>
          <th className={styles.personal}>Accrued</th>
          <th className={styles.personal}>Used</th>
          <th className={styles.personal}>Used Ytd</th>
          <th className={styles.personal}>Avail</th>
          <th className={styles.vacation}>Rate</th>
          <th className={styles.vacation}>Accrued</th>
          <th className={styles.vacation}>Used</th>
          <th className={styles.vacation}>Used Ytd</th>
          <th className={styles.vacation}>Avail</th>
          <th className={styles.sick}>Rate</th>
          <th className={styles.sick}>Accrued</th>
          <th className={styles.sick}>Used</th>
          <th className={styles.sick}>Used Ytd</th>
          <th className={styles.sick}>Avail</th>
        </tr>
      </thead>
      <tbody>
        {records.map((record) => (
          <tr
            key={record.payPeriod?.endDate}
            className={
              record.payPeriod?.current ? styles.highlighted : undefined
            }
            title="Open a Printable View for this Record"
            onClick={() => onRecordClick(record)}
          >
            <td>{record.payPeriod?.payPeriodNum}</td>
            <td>{formatDate(record.payPeriod?.endDate)}</td>
            <HourCell type="personal">{record.personalAccruedYtd}</HourCell>
            <HourCell type="personal">{record.biweekPersonalUsed}</HourCell>
            <HourCell type="personal">{record.personalUsed}</HourCell>
            <HourCell type="personal" available>
              {record.personalAvailable}
            </HourCell>
            <HourCell type="vacation">{record.vacationRate}</HourCell>
            <HourCell type="vacation">
              {sum(record.vacationAccruedYtd, record.vacationBanked)}
            </HourCell>
            <HourCell type="vacation">{record.biweekVacationUsed}</HourCell>
            <HourCell type="vacation">{record.vacationUsed}</HourCell>
            <HourCell type="vacation" available>
              {record.vacationAvailable}
            </HourCell>
            <HourCell type="sick">{record.sickRate}</HourCell>
            <HourCell type="sick">{record.sickAccruedYtd}</HourCell>
            <HourCell type="sick">
              {sum(
                record.biweekSickEmpUsed,
                record.biweekSickFamUsed,
                record.biweekSickDonated,
              )}
            </HourCell>
            <HourCell type="sick">
              {sum(record.sickEmpUsed, record.sickFamUsed, record.sickDonated)}
            </HourCell>
            <HourCell type="sick" available>
              {record.sickAvailable}
            </HourCell>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export function HourCell({ type, available, changed, children }) {
  const classNames = [styles.accrualHours, styles[type]];
  if (available) {
    classNames.push(styles.availableHours);
  }
  if (changed) {
    classNames.push(styles.changed);
  }
  return <td className={classNames.join(" ")}>{children}</td>;
}

function formatDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd/yyyy") : "";
}

/** Adds hour values, dropping the floating point noise a plain sum would show. */
function sum(...values) {
  const total = values.reduce((acc, value) => acc + (value || 0), 0);
  return Math.round(total * 100) / 100;
}
