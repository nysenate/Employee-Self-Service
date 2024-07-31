//AccrualBar.js
import React, { useEffect, useState } from "react";
import { fetchAccruals } from "app/views/time/accrual/time-accrual-ctrl";
import styles from "../universalStyles.module.css"
import LoadingIndicator from "app/components/LoadingIndicator";
import { hoursDiffHighlighter } from "app/views/time/helpers";

export default function AccrualBar({ empId }) {
  const [accruals, setAccruals] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const formattedDate = `${year}-${month}-${day}`;
        const params = {
          beforeDate: formattedDate,
          empId: empId,
        };
        const response = await fetchAccruals(params);
        setAccruals(response.result);
      } catch (err) {
        console.error("Error fetching Accruals in AccrualBar", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [empId]);

  return (
    <div>
      {!(!loading && accruals) ? (
        <LoadingIndicator/>
      ) : (
         <div className={styles.accrualHoursContainer}>
           <div className={styles.accrualComponent}>
             <div className={styles.captionedHourSquare} style={{ float: "left" }}>
               <div className={`${styles.hoursCaption} ${styles.personal}`}>Personal Hours</div>
               <div className={styles.hoursDisplay}>{accruals?.personalAvailable}</div>
             </div>
           </div>
           <div className={styles.accrualComponent}>
             <div className={styles.captionedHourSquare} style={{ float: "left" }}>
               <div className={`${styles.hoursCaption} ${styles.vacation}`}>Vacation Hours</div>
               <div className={styles.hoursDisplay}>{accruals?.vacationAvailable}</div>
             </div>
           </div>
           <div className={styles.accrualComponent}>
             <div className={styles.captionedHourSquare} style={{ float: "left" }}>
               <div className={`${styles.hoursCaption} ${styles.sick}`}>Sick Hours</div>
               <div className={`${styles.odometer} ${styles.hoursDisplay}`}>{accruals?.sickAvailable}</div>
             </div>
           </div>
           <div className={styles.accrualComponent}>
             <div className={styles.captionedHourSquare} style={{ width: "390px" }}>
               <div style={{ background: "#5c7474", color: "white" }} className={styles.hoursCaption}>
                 Year To Date Hours Of Service
               </div>
               <div className={styles.hoursDisplay} style={{ fontSize: "1em" }}>
                 <div className={styles.ytdHours}>Expected: {accruals?.serviceYtdExpected}</div>
                 <div className={styles.ytdHours}>Actual: {accruals?.serviceYtd}</div>
                 <div className={styles.ytdHours} style={{ borderRight: "none" }}>
                   Difference: <span>{hoursDiffHighlighter(accruals)}</span>
                 </div>
               </div>
             </div>
           </div>
           <div style={{ clear: "both" }}></div>
         </div>
       )}
    </div>
  );
}