import React from 'react';
import styles from './OrderHistoryIndex.module.css';

const SubHero = ({ fromDate, setFromDate, toDate, setToDate, status, setStatus, statusOptions }) => {
  const handleStatusChange = (event) => {
    setStatus(event.target.value);
  };

  return (
    <div className={`${styles.contentContainer} ${styles.contentControls}`}>
      <h4 className={`${styles.contentInfo} ${styles.supplyText}`} style={{marginBottom: '0px'}}>
        Search order history by date or status.
      </h4>
      <div className={styles.grid}>
        <div className={`${styles.col412} ${styles.paddingX}`} style={{paddingTop: '50px'}}>
          <label className={styles.bold}>From: </label>
          <input
            type="date"
            id="from-date"
            name="from-date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
          {/*<img className={styles.uiDatePickerTrigger} src={"/assets/img/calendar.png"}/>*/}
        </div>
        <div className={`${styles.col412} ${styles.paddingX}`} style={{paddingTop: '50px'}}>
          <label className={styles.bold}>To: </label>
          <input
            type="date"
            id="to-date"
            name="to-date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
          {/*<img className={styles.uiDatePickerTrigger} src={"/assets/img/calendar.png"}/>*/}
        </div>
        <div className={`${styles.col412} ${styles.paddingX}`} style={{paddingTop: '50px'}}>
          <label className={styles.bold} style={{display: 'inline-block', verticalAlign: 'middle'}}>
            Status:
          </label>
          <select id="status" name="status" value={status} onChange={handleStatusChange}>
            {statusOptions.map(option => (
              <option key={option} value={option}>{option}</option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
};

export default SubHero;
