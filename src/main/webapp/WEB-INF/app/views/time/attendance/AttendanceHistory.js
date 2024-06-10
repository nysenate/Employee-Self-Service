import React, { useState, useEffect } from 'react';
import Hero from "../../../components/Hero";
import styles from './AttendanceHistory.module.css';
import { fetchAttendanceRecords } from '../../../views/time/services/attendanceService';

const SubHero = ({ selectedYear, handleYearChange }) => {
  return (
    <div className={styles.subHeroContainer}>
      <div className={styles.subHeroMessage}>
        View attendance records for year
        <select
          className={styles.selectYear}
          value={selectedYear}
          onChange={handleYearChange}
        >
          {Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i).map(year => (
            <option key={year} value={year}>{year}</option>
          ))}
        </select>
      </div>
    </div>
  );
};

const ActiveAttendanceRecords = ({ records }) => (
  <div className={styles.recordSection}>
    <div className={styles.sectionHeader}>Active Attendance Records</div>
    <div className={styles.sectionDescription}>
      The following time records are in progress or awaiting submission. Click a row to view the in-progress record.
    </div>
    <table className={styles.attendanceTable}>
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
        {records.active.map((record, index) => (
          <tr key={index}>
            <td>{record.dateRange}</td>
            <td>{record.payPeriod}</td>
            <td>{record.status}</td>
            <td>{record.work}</td>
            <td>{record.holiday}</td>
            <td>{record.vacation}</td>
            <td>{record.personal}</td>
            <td>{record.sickEmp}</td>
            <td>{record.sickFam}</td>
            <td>{record.misc}</td>
            <td>{record.total}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);

const SubmittedAttendanceRecords = ({ records }) => {
  const annualTotals = records.submitted.reduce(
    (totals, record) => {
      totals.work += record.work;
      totals.holiday += record.holiday;
      totals.vacation += record.vacation;
      totals.personal += record.personal;
      totals.sickEmp += record.sickEmp;
      totals.sickFam += record.sickFam;
      totals.misc += record.misc;
      totals.total += record.total;
      return totals;
    },
    {
      work: 0,
      holiday: 0,
      vacation: 0,
      personal: 0,
      sickEmp: 0,
      sickFam: 0,
      misc: 0,
      total: 0
    }
  );

  return (
    <div className={styles.recordSection}>
      <div className={styles.sectionHeader}>Submitted Attendance Records</div>
      <div className={styles.sectionDescription}>
        Time records that have been submitted for pay periods during the selected year are listed in the table below. You can view details about each pay period by clicking the row.
      </div>
      <table className={styles.attendanceTable}>
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
          {records.submitted.map((record, index) => (
            <tr key={index}>
              <td>{record.dateRange}</td>
              <td>{record.payPeriod}</td>
              <td style={{color: record.status === 'Personnel Approved' ? 'green' : 'inhert'}}>
                {record.status}
              </td>
              <td>{record.work}</td>
              <td>{record.holiday}</td>
              <td>{record.vacation}</td>
              <td>{record.personal}</td>
              <td>{record.sickEmp}</td>
              <td>{record.sickFam}</td>
              <td>{record.misc}</td>
              <td>{record.total}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr className={styles.attendanceTotals}>
            <td colSpan="3">Annual Totals</td>
            <td>{annualTotals.work}</td>
            <td>{annualTotals.holiday}</td>
            <td>{annualTotals.vacation}</td>
            <td>{annualTotals.personal}</td>
            <td>{annualTotals.sickEmp}</td>
            <td>{annualTotals.sickFam}</td>
            <td>{annualTotals.misc}</td>
            <td>{annualTotals.total}</td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
};

export default function AttendanceHistory() {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [attendanceRecords, setAttendanceRecords] = useState({ active: [], submitted: [] });

  useEffect(() => {
    const fetchData = async () => {
      const data = await fetchAttendanceRecords(selectedYear);
      setAttendanceRecords(data);
    };
    fetchData();
  }, [selectedYear]);

  return (
    <div>
      <Hero>Attendance History</Hero>
      <SubHero selectedYear={selectedYear} handleYearChange={(e) => setSelectedYear(e.target.value)} />
      <ActiveAttendanceRecords records={attendanceRecords} />
      <SubmittedAttendanceRecords records={attendanceRecords} />
    </div>
  );
}
