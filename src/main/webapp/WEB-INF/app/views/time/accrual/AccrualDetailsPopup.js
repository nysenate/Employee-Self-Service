// AccrualDetailsPopup.js
import React from 'react';
import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import styles from "../universalStyles.module.css";
import { useNavigate } from "react-router-dom";
import { formatDateToMMDDYYYY, hoursDiffHighlighter } from "app/views/time/helpers";

// Needs:
//    -handle Print
export function AccrualDetailsPopup({ accruals, isModalOpen, closeModal }) {
  const navigate = useNavigate();

  if(!accruals) console.log("NADA");
  if(accruals) console.log("Loaded: ", accruals);

  const handlePrintClick = () => {
    // Angular: href={`${ctxPath}/api/v1/accrual/report?empId=${accruals.empId}&date=${accruals.payPeriod.endDate}`}
    // Ex React: navigate(`/supply/order-history/order/${requisition.requisitionId}`, { state: { order: requisition, print: true } });
    console.log('Print Accrual');
  };

  const Title = ({ accruals }) => {
    return `${accruals.computed && !accruals.submitted ? 'Projected ' : ''}
    Accrual Usage for ${new Date(accruals.payPeriod.startDate).getFullYear()}
    Pay Period ${accruals.payPeriod.payPeriodNum}`;
  }


  return (
    <Popup
      isLocked={false}
      isOpen={isModalOpen}
      onClose={closeModal}
      title={Title({ accruals })}
    >
      <div className={styles.grid}>
        <div className={`${styles.accrualDetailContent} ${styles.col1212}`}>
          <div className={`${styles.col1012} ${styles.accrualDetailTableContainer}`}>
            <div className={`${styles.col612}`}>
              <h4 className={styles.contentInfo}>YTD Hours of Service</h4>
              <table className={styles.accrualDetailTable}>
                <tbody>
                <tr>
                  <td>Expected</td>
                  <td>{accruals.serviceYtdExpected.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Actual</td>
                  <td>{accruals.serviceYtd.toFixed(2)}</td>
                </tr>
                <tr className={styles.totalRow}>
                  <td>Difference</td>
                  <td>{hoursDiffHighlighter(accruals)}</td>
                </tr>
                </tbody>
              </table>
              <h4 className={`${styles.contentInfo} ${styles.sick}`}>Sick Hours</h4>
              <table className={styles.accrualDetailTable}>
                <tbody>
                <tr>
                  <td>Prev. Year Banked</td>
                  <td>{accruals.sickBanked.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Accrued YTD</td>
                  <td>{accruals.sickAccruedYtd.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used YTD (Employee)</td>
                  <td>{(-accruals.sickEmpUsed).toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used YTD (Family)</td>
                  <td>{(-accruals.sickFamUsed).toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Donated YTD</td>
                  <td>{(-accruals.sickDonated).toFixed(2)}</td>
                </tr>
                <tr className={styles.totalRow}>
                  <td>Available for Period</td>
                  <td>{accruals.sickAvailable.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used in Period (Employee)</td>
                  <td>{(-accruals.biweekSickEmpUsed).toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used in Period (Family)</td>
                  <td>{(-accruals.biweekSickFamUsed).toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Donated in Period</td>
                  <td>{(-accruals.biweekSickDonated).toFixed(2)}</td>
                </tr>
                </tbody>
              </table>
            </div>
            <div className={`${styles.col612}`}>
              <h4 className={`${styles.contentInfo} ${styles.personal}`}>Personal Hours</h4>
              <table className={styles.accrualDetailTable}>
                <tbody>
                <tr>
                  <td>Accrued YTD</td>
                  <td>{accruals.personalAccruedYtd.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used YTD</td>
                  <td>{(-accruals.personalUsed).toFixed(2)}</td>
                </tr>
                <tr className={styles.totalRow}>
                  <td>Available for Period</td>
                  <td>{accruals.personalAvailable.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used in Period</td>
                  <td>{accruals.biweekPersonalUsed.toFixed(2)}</td>
                </tr>
                </tbody>
              </table>
              <h4 className={`${styles.contentInfo} ${styles.vacation}`}>Vacation Hours</h4>
              <table className={styles.accrualDetailTable}>
                <tbody>
                <tr>
                  <td>Prev. Year Banked</td>
                  <td>{accruals.vacationBanked.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Accrued YTD</td>
                  <td>{accruals.vacationAccruedYtd.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used YTD</td>
                  <td>{(-accruals.vacationUsed).toFixed(2)}</td>
                </tr>
                <tr className={styles.totalRow}>
                  <td>Available for Period</td>
                  <td>{accruals.vacationAvailable.toFixed(2)}</td>
                </tr>
                <tr>
                  <td>Used in Period</td>
                  <td>{accruals.biweekVacationUsed.toFixed(2)}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div className={`${styles.col212} ${styles.accrualDetailInfo}`}>
            <h4 className={styles.contentInfo}>Period Dates</h4>
            <table>
              <tbody>
              <tr>
                <th>Begin</th>
                <td>{formatDateToMMDDYYYY(accruals.payPeriod.startDate)}</td>
              </tr>
              <tr>
                <th>End</th>
                <td>{formatDateToMMDDYYYY(accruals.payPeriod.endDate)}</td>
              </tr>
              </tbody>
            </table>
            <h4 className={styles.contentInfo}>Acc. Rates</h4>
            <table className={styles.accrualRateTable}>
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
            <h4 className={styles.contentInfo}>Actions</h4>
            <p className={styles.accrualReportLink}>
              <a
                target="_blank"
                rel="noopener noreferrer"
                title="Open a Printable View for this Record"
                // href={`${ctxPath}/api/v1/accrual/report?empId=${accruals.empId}&date=${accruals.payPeriod.endDate}`}
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePrintClick();
                }}
              >
                Print Report
              </a>
              <br />
              <br />
              <a href="#" onClick={closeModal} title="Close this Window">
                Exit
              </a>
            </p>
          </div>
        </div>
      </div>
    </Popup>
  );
}