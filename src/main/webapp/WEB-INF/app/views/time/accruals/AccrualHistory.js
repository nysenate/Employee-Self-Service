import React, { useState, useEffect } from 'react';
import Hero from "../../../components/Hero";
import styles from './AccrualHistory.module.css';
import { fetchAccrualRecords } from '../../../views/time/services/attendanceService';

const SubHero = ({ selectedYear, handleYearChange }) => (
  <div className={styles.subHeroContainer}>
    <div className={styles.subHeroMessage}>
      Filter By Year
      <select className={styles.selectYear} value={selectedYear} onChange={handleYearChange}>
        {Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i).map(year => (
          <option key={year} value={year}>{year}</option>
        ))}
      </select>
    </div>
  </div>
);

const AccrualRecords = ({ records }) => (
  <div className={styles.accrualHistory}>
    <div className={styles.tableContainer}>
      <div className={`${styles.table} ${styles.period}`}>
        <div className={styles.tableHeader}>
          <div className={styles.cell}>Pay Period</div>
        </div>
        <div className={styles.tableSubHeader}>
          <div className={styles.cell}>#</div>
          <div className={styles.cell}>End Date</div>
        </div>
        <div className={styles.tableBody}>
          {records.map((row, index) => (
            <div key={index} className={styles.row}>
              <div className={styles.cell}>{row.period}</div>
              <div className={styles.cell}>{row.endDate}</div>
            </div>
          ))}
        </div>
      </div>

      <div className={`${styles.table} ${styles.personal}`}>
        <div className={styles.tableHeader}>
          <div className={styles.cell}>Personal Hours</div>
        </div>
        <div className={styles.tableSubHeader}>
          <div className={styles.cell}>Accrued</div>
          <div className={styles.cell}>Used</div>
          <div className={styles.cell}>Used YTD</div>
          <div className={styles.cell}>Avail</div>
        </div>
        <div className={styles.tableBody}>
          {records.map((row, index) => (
            <div key={index} className={styles.row}>
              <div className={styles.cell}>{row.personal.accrued}</div>
              <div className={styles.cell}>{row.personal.used}</div>
              <div className={styles.cell}>{row.personal.usedYTD}</div>
              <div className={styles.personalAvail}>{row.personal.avail}</div>
            </div>
          ))}
        </div>
      </div>

      <div className={`${styles.table} ${styles.vacation}`}>
        <div className={styles.tableHeader}>
          <div className={styles.cell}>Vacation Hours</div>
        </div>
        <div className={styles.tableSubHeader}>
          <div className={styles.cell}>Accrued</div>
          <div className={styles.cell}>Used</div>
          <div className={styles.cell}>Used YTD</div>
          <div className={styles.cell}>Avail</div>
        </div>
        <div className={styles.tableBody}>
          {records.map((row, index) => (
            <div key={index} className={styles.row}>
              <div className={styles.cell}>{row.vacation.accrued}</div>
              <div className={styles.cell}>{row.vacation.used}</div>
              <div className={styles.cell}>{row.vacation.usedYTD}</div>
              <div className={styles.vacationAvail}>{row.vacation.avail}</div>
            </div>
          ))}
        </div>
      </div>

      <div className={`${styles.table} ${styles.sick}`}>
        <div className={styles.tableHeader}>
          <div className={styles.cell}>Sick Hours</div>
        </div>
        <div className={styles.tableSubHeader}>
          <div className={styles.cell}>Accrued</div>
          <div className={styles.cell}>Used</div>
          <div className={styles.cell}>Used YTD</div>
          <div className={styles.cell}>Avail</div>
        </div>
        <div className={styles.tableBody}>
          {records.map((row, index) => (
            <div key={index} className={styles.row}>
              <div className={styles.cell}>{row.sick.accrued}</div>
              <div className={styles.cell}>{row.sick.used}</div>
              <div className={styles.cell}>{row.sick.usedYTD}</div>
              <div className={styles.sickAvail}>{row.sick.avail}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  </div>
);

export default function AccrualHistory() {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [accrualRecords, setAccrualRecords] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const data = await fetchAccrualRecords(selectedYear);
      setAccrualRecords(data);
    };
    fetchData();
  }, [selectedYear]);

  return (
    <div>
      <Hero>Accrual History</Hero>
      <SubHero selectedYear={selectedYear} handleYearChange={(e) => setSelectedYear(e.target.value)} />
      <AccrualRecords records={accrualRecords} />
    </div>
  );
}
