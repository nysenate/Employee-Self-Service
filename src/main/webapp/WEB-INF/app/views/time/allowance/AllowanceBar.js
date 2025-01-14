// AllowanceBar.js
import { hoursDiffHighlighterCustom } from "app/views/time/helpers";
import styles from "../universalStyles.module.css";
import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { getAvailableHours } from "app/views/time/allowance/time-allowance-ctrl";

export default function AllowanceBar({ allowance, tempWorkHours=null, loading=null, submitted=false}) {

  console.log("bar allow",allowance);
  const getThisAvailableHours = () => {
    let hours = submitted ? 0 : tempWorkHours || 0;
    console.log('hours',hours)
    return getAvailableHours(allowance, hours);
  }

  const showRecordHours = () => {
    if(tempWorkHours) return isNumber(tempWorkHours)
    return false;
  }

  return (
    <div>
      {!allowance || loading ? (
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
                   {allowance?.totalHours.toLocaleString()}
                 </div>
                 <div className={styles.ytdHours}>
                   <div className={styles.hoursCaption}>Reported Hours</div>
                   {allowance?.hoursUsed?.toLocaleString()}
                 </div>
                 {showRecordHours() && (
                   <div className={styles.ytdHours}>
                     <div className={styles.hoursCaption}>Current Record Hours</div>
                     {tempWorkHours?.toLocaleString()}
                   </div>
                 )}
                 <div className={styles.ytdHours}>
                   <div className={styles.hoursCaption}>Estimated Available Hours</div>
                   {hoursDiffHighlighterCustom(getThisAvailableHours(),0)}
                 </div>
               </div>
             </div>
           </div>
         </div>
       )}
    </div>
  );
}
