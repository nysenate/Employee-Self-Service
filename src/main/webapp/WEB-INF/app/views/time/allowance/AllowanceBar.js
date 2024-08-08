// AllowanceBar.js
import { hoursDiffHighlighterCustom } from "app/views/time/helpers";
import styles from "../universalStyles.module.css";
import React, { useEffect, useState } from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { fetchAllowance } from "app/views/time/allowance/time-allowance-ctrl";

//
// http://localhost:8080/api/v1/allowances?empId=13612&year=2024
//
// Total Allowed Hours=yearlyAllowance/salaryRecs.salaryRate     Reported Hours=hoursUsed (idk theyre same)       Estimated Available Hours=T-R

// In angular, allowance is passed in
export default function AllowanceBar({ empId, tempWorkHours}) {
  const [allowance, setAllowance] = useState();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if(!empId) return;
    const fetchData = async () => {
      setLoading(true);
      try {
        const today = new Date();
        const year = today.getFullYear();
        const formattedDate = `${year}`;
        const params = {
          year: formattedDate,
          empId: empId,
        };
        const response = await fetchAllowance(params);
        console.log("helo",response.result[0]);
        setAllowance(response.result[0]);
      } catch (err) {
        console.error("Error fetching Allowance in AllowanceBar", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [empId]);

  // const showRecordHours = () => {
  // };

  const getMaxSalaryRate = () => {
    if (allowance.salaryRecs.length > 1) {
      let max = 0;
      allowance.salaryRecs.forEach((rec) => {
        max = Math.max(max, rec.salaryRate);
      });
      return Math.floor(max * 4) / 4;
    }
    return Math.floor(allowance.salaryRecs[0].salaryRate * 4) / 4;
  }

  const getTotalAllowedHours = () => {
    if (allowance) {
      const totalAllowedHours = allowance.yearlyAllowance/getMaxSalaryRate();
      return Math.floor(totalAllowedHours*4)/4;
    }
    return 0;
  };
  const getAvailableHours = () => {
    return hoursDiffHighlighterCustom(getTotalAllowedHours(),allowance?.hoursUsed);
  }

  // const showRecordHours = () => {
  //   if(tempWorkHours) return isNumber(tempWorkHours)
  //   return false;
  // }

  return (
    <div>
      {loading ? (
        <LoadingIndicator/>
      ) : (
         <div className={styles.allowanceContainer}>
           <div className={styles.allowanceComponent}>
             <div className={styles.captionedHourSquare}>
               <div className={styles.hoursCaption}>
                 {allowance?.year} Allowance
               </div>
               <div className={styles.hoursDisplay}>
                 <div className={styles.ytdHours}>
                   <div className={styles.hoursCaption}>Total Allowed Hours</div>
                   {getTotalAllowedHours().toLocaleString()}
                 </div>
                 <div className={styles.ytdHours}>
                   <div className={styles.hoursCaption}>Reported Hours</div>
                   {allowance?.hoursUsed?.toLocaleString()}
                 </div>
                 {/*I really dont know what tempworkhours is or where its comming from*/}
                 {/*{showRecordHours() && (*/}
                 {/*  <div className={styles.ytdHours}>*/}
                 {/*    <div className={styles.hoursCaption}>Current Record Hours</div>*/}
                 {/*    {allowance?.tempWorkHours?.toLocaleString()}*/}
                 {/*  </div>*/}
                 {/*)}*/}
                 <div className={styles.ytdHours}>
                   <div className={styles.hoursCaption}>Estimated Available Hours</div>
                   {getAvailableHours()}
                 </div>
               </div>
             </div>
           </div>
         </div>
       )}
    </div>
  );
}
