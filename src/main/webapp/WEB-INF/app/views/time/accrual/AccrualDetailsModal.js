import React from "react";
import { format, parseISO } from "date-fns";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import { HoursDiff } from "app/views/time/attendance/record-entry/HourSquare";
import styles from "app/views/time/accrual/accrual.module.css";

/**
 * A detailed breakdown of one pay period's accrual usage, opened by clicking a row of the
 * accrual history or accrual projections table.
 *
 * Ported from the legacy accrual details modal
 * (WEB-INF/view/template/time/accrual/accrual-details.jsp).
 *
 * @param accruals The accrual summary to show, or null when the modal is closed.
 * @param onClose Called when the modal is dismissed.
 */
export default function AccrualDetailsModal({ accruals, onClose }) {
  if (!accruals) {
    return null;
  }

  const projected = accruals.computed && !accruals.submitted;
  const payPeriod = accruals.payPeriod || {};

  return (
    <Modal isOpen={!!accruals} onOpenChange={onClose} className="max-w-[95vw]">
      <Modal.Title>
        {projected && "Projected "}Accrual Usage for{" "}
        {payPeriod.startDate?.substring(0, 4)} Pay Period{" "}
        {payPeriod.payPeriodNum}
      </Modal.Title>
      <Modal.Body>
        <div className="flex flex-col gap-5 lg:flex-row">
          <div className="flex flex-col gap-5 sm:flex-row">
            <div>
              <SectionHeader>YTD Hours of Service</SectionHeader>
              <table className={styles.detailTable}>
                <tbody>
                  <DetailRow
                    label="Expected"
                    value={accruals.serviceYtdExpected}
                  />
                  <DetailRow label="Actual" value={accruals.serviceYtd} />
                  <tr className={styles.totalRow}>
                    <td>Difference</td>
                    <td>
                      {/* Formatted before it is handed over so the sign carries two decimals. */}
                      <HoursDiff
                        hours={formatHours(
                          accruals.serviceYtd - accruals.serviceYtdExpected,
                        )}
                      />
                    </td>
                  </tr>
                </tbody>
              </table>

              <SectionHeader variant="sick">Sick Hours</SectionHeader>
              <table className={styles.detailTable}>
                <tbody>
                  <DetailRow
                    label="Prev. Year Banked"
                    value={accruals.sickBanked}
                  />
                  <DetailRow
                    label="Accrued YTD"
                    value={accruals.sickAccruedYtd}
                  />
                  <DetailRow
                    label="Used YTD (Employee)"
                    value={-accruals.sickEmpUsed}
                  />
                  <DetailRow
                    label="Used YTD (Family)"
                    value={-accruals.sickFamUsed}
                  />
                  <DetailRow
                    label="Donated YTD"
                    value={-accruals.sickDonated}
                  />
                  <DetailRow
                    label="Available for Period"
                    value={accruals.sickAvailable}
                    isTotal
                  />
                  <DetailRow
                    label="Used in Period (Employee)"
                    value={-accruals.biweekSickEmpUsed}
                  />
                  <DetailRow
                    label="Used in Period (Family)"
                    value={-accruals.biweekSickFamUsed}
                  />
                  <DetailRow
                    label="Donated in Period"
                    value={-accruals.biweekSickDonated}
                  />
                </tbody>
              </table>
            </div>

            <div>
              <SectionHeader variant="personal">Personal Hours</SectionHeader>
              <table className={styles.detailTable}>
                <tbody>
                  <DetailRow
                    label="Accrued YTD"
                    value={accruals.personalAccruedYtd}
                  />
                  <DetailRow label="Used YTD" value={-accruals.personalUsed} />
                  <DetailRow
                    label="Available for Period"
                    value={accruals.personalAvailable}
                    isTotal
                  />
                  <DetailRow
                    label="Used in Period"
                    value={accruals.biweekPersonalUsed}
                  />
                </tbody>
              </table>

              <SectionHeader variant="vacation">Vacation Hours</SectionHeader>
              <table className={styles.detailTable}>
                <tbody>
                  <DetailRow
                    label="Prev. Year Banked"
                    value={accruals.vacationBanked}
                  />
                  <DetailRow
                    label="Accrued YTD"
                    value={accruals.vacationAccruedYtd}
                  />
                  <DetailRow label="Used YTD" value={-accruals.vacationUsed} />
                  <DetailRow
                    label="Available for Period"
                    value={accruals.vacationAvailable}
                    isTotal
                  />
                  <DetailRow
                    label="Used in Period"
                    value={accruals.biweekVacationUsed}
                  />
                </tbody>
              </table>
            </div>
          </div>

          <div className="w-full shrink-0 lg:w-48">
            <SectionHeader>Period Dates</SectionHeader>
            <table className={`${styles.detailInfoTable} w-full`}>
              <tbody>
                <tr>
                  <th>Begin</th>
                  <td>{formatDate(payPeriod.startDate)}</td>
                </tr>
                <tr>
                  <th>End</th>
                  <td>{formatDate(payPeriod.endDate)}</td>
                </tr>
              </tbody>
            </table>

            <SectionHeader>Acc. Rates</SectionHeader>
            <table className={`${styles.detailInfoTable} w-full`}>
              <tbody>
                <tr>
                  <th>Vacation</th>
                  <td>{accruals.vacationRate}</td>
                </tr>
                <tr>
                  <th>Sick</th>
                  <td>{accruals.sickRate}</td>
                </tr>
              </tbody>
            </table>

            <SectionHeader>Actions</SectionHeader>
            <p className="pt-2 text-center">
              <a
                href={`/api/v1/accrual/report?empId=${accruals.empId}&date=${payPeriod.endDate}`}
                target="_blank"
                rel="noreferrer"
                title="Open a Printable View for this Record"
              >
                Print Report
              </a>
            </p>
          </div>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={() => onClose(false)}>
          Exit
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/*
 * The legacy headings are h4.content-info: a centered bar under the teal rule, which the
 * accrual type headings fill with their own color (assets/css/less/time.less).
 */
const HEADER_VARIANTS = {
  sick: "bg-[#d19525] text-white",
  personal: "bg-[#4196a7] text-white",
  vacation: "bg-[#799933] text-white",
};

function SectionHeader({ variant, children }) {
  return (
    <h4
      className={`mt-4 border-b border-teal-600/25 p-2 text-center font-semibold first:mt-0 ${
        HEADER_VARIANTS[variant] || "text-gray-900"
      }`}
    >
      {children}
    </h4>
  );
}

function DetailRow({ label, value, isTotal }) {
  return (
    <tr className={isTotal ? styles.totalRow : undefined}>
      <td>{label}</td>
      <td>{formatHours(value)}</td>
    </tr>
  );
}

/** The legacy tables displayed every hour value with the "number:2" filter. */
function formatHours(hours) {
  const value = Number(hours);
  if (isNaN(value)) {
    return "";
  }
  // Negating a zero total would otherwise print as "-0.00".
  return (value === 0 ? 0 : value).toFixed(2);
}

function formatDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd/yyyy") : "";
}
