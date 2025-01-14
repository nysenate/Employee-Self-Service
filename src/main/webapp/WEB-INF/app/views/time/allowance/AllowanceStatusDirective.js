import React, { useState, useEffect} from "react";
import styles from "../universalStyles.module.css";
import LoadingIndicator from "app/components/LoadingIndicator";
import AllowanceBar from "app/views/time/allowance/AllowanceBar";
import { computeRemaining, fetchAllowance } from "app/views/time/allowance/time-allowance-ctrl";


export default function AllowanceStatusDirective({
                                                   viewDetails,
                                                   user,
                                                   empSupInfo,
                                                   scopeHideTitle,
                                                 }) {
  const [empId, actualSetEmpId] = useState(null);
  const [allowance, setAllowance] = useState(null);
  const [payType, setPayType] = useState(null);
  const [request, setRequest] = useState({allowance: false})

  const hideTitle = scopeHideTitle || false;

  /* --- Watches --- */
  useEffect(() => {
    setEmpId();
  }, [empSupInfo]);

  useEffect(() => {
    if(!empId) return;
    const fetchData = async () => {
      setRequest({ allowance: true });
      try {
        const today = new Date();
        const year = today.getFullYear();
        const formattedDate = `${year}`;
        const params = {
          year: formattedDate,
          empId: empId,
        };
        const response = await fetchAllowance(params);
        let results = response.result || [];
        if (results.length !== 1) {
          console.log('fetchAllowance returned empty [] with params: ', params);
          return;
        }

        let dateRange = {
          beginDate: new Date(),
          endDate: new Date(),
        }
        let tempAllowance = response.result[0];
        console.log(tempAllowance);
        computeRemaining(tempAllowance, dateRange);
        console.log(tempAllowance);
        setAllowance(tempAllowance);

        extractCurrentPayType();
      } catch (err) {
        console.error("Error fetching Allowance in AllowanceBar", err);
      } finally {
        setRequest({ allowance: false });
      }
    };

    fetchData();
  }, [empId]);


  /* --- Display Methods --- */
  const isUser = () => {
    return empSupInfo?.employeeId === user.employeeId || empSupInfo?.empId === user.employeeId;
  };

  /* --- Internal Methods --- */

  const extractCurrentPayType = () => {
    if(!allowance && allowance.salaryRecs) {
      console.error('No Salary Recs!!');
      return;
    }

    allowance.salaryRecs.forEach((salaryRec) => {
      let startDate = new Date(salaryRec.effectDate || 0);
      let endDate = new Date(salaryRec.endDate || '3000-01-01');
      const today = new Date();

      if(today < startDate || today > endDate) return;
      setPayType(salaryRec.payType);
    });
  }

  /* Set the employee id from the passed in employee sup info if it exists
   * Otherwise set it to the user's empId
   * REQUIRES empSupInfo */
  const setEmpId = () => {
    let thisEmpId = null;
    if(empSupInfo && (empSupInfo?.empId || empSupInfo?.employeeId)) {
      thisEmpId = empSupInfo?.empId || empSupInfo?.employeeId;
    } else {
      thisEmpId = user.employeeId;
    }
    actualSetEmpId(thisEmpId);
  }


  return (
    <>
      {request.allowance ?
       (
         <LoadingIndicator/>
       ) : (
         <div className={styles.contentContainer}>
           {!(hideTitle || isUser()) && (<div className={`${styles.contentContainer} ${styles.contentControls}`}>
             <h1>
               {empSupInfo.empFirstName}
               {empSupInfo.empLastName}'s
               Current Allowed Hours
             </h1>
           </div>)}

           {payType !== 'TE' && (<p className={styles.contentInfo}>
             Selected employee is non-temporary and does not have an allowance.
           </p>)}

           <div className={styles.paddingX}>
             {payType === 'TE' || payType === null && (<AllowanceBar allowance={allowance}/>)}
           </div>
         </div>
       )}
    </>
  )
}