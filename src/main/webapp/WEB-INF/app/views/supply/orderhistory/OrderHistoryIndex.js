import React, { useState, useEffect } from "react";
import Hero from "../../../components/Hero";
import styles from './OrderHistoryIndex.module.css';

const SubHero = () => {
  const getCurrentDate = () => {
    const today = new Date();
    const dd = String(today.getDate()).padStart(2, '0');
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // January is 0!
    const yyyy = today.getFullYear();
    return `${yyyy}-${mm}-${dd}`;
  };

  const getOneMonthBeforeDate = () => {
    const today = new Date();
    today.setMonth(today.getMonth() - 1);
    const dd = String(today.getDate()).padStart(2, '0');
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // January is 0!
    const yyyy = today.getFullYear();
    return `${yyyy}-${mm}-${dd}`;
  };

  const [fromDate, setFromDate] = useState(getOneMonthBeforeDate());
  const [toDate, setToDate] = useState(getCurrentDate());

  return (
    <div className={styles.subHeroContainer}>
      {/* Message */}
      <div className={styles.subHeroMessage}>Search order history by date or status.</div>
      {/* Search Abilities */}
      <div className={styles.searchContainer}>
        <div className={styles.searchItem}>
          <label htmlFor="from-date">From:</label>
          <input
            type="date"
            id="from-date"
            name="from-date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </div>
        <div className={styles.searchItem}>
          <label htmlFor="to-date">To:</label>
          <input
            type="date"
            id="to-date"
            name="to-date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>
        <div className={styles.searchItem}>
          <label htmlFor="status">Status:</label>
          <select id="status" name="status">
            <option value="all">ALL</option>
            <option value="pending">PENDING</option>
            <option value="processing">PROCESSING</option>
            <option value="completed">COMPLETED</option>
            <option value="approved">APPROVED</option>
            <option value="rejected">REJECTED</option>
          </select>
        </div>
      </div>
    </div>
  );
};

function Results() {
  const empty = true;

  return (
    <div className={styles.resultsContainer}>
      {empty ?
       (
         <div className={styles.noResults}>
           No results were found.
         </div>
       ) : (
         <div>
           Results were found!
         </div>
       )
      }
    </div>
  )
}

export default function OrderHistoryIndex() {
  return (
    <div>
      <Hero>Order History</Hero>
      <SubHero />
      <Results />
    </div>
  )
}
