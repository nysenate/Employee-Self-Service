// RecordEntryDirective.js
/**
 * This template provides all time record entry functionality for both regular/special annual time records
 * as well as temporary time records.
 */
import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import EssNotification from "app/components/EssNotification";
import styles from "../universalStyles.module.css";
import AnnualEntryForm from "app/views/time/record/AnnualEntryForm";
import { useRecordEntryCtrl } from "app/views/time/record/record-entry-ctrl";


// API's with annual: /timerecords/active? , /holidays? , /accruals? , /expectedhrs? , /miscleave/grantsWithRemainingHours?
//  with temp: timerecords , holi , /allowances? , micleave
export default function RecordEntryDirective() {
  const { state, setState } = useRecordEntryCtrl();
  useEffect(() => {
    console.log(state);
  }, [state]);

  return (
    <>
      <Hero>Attendance Record Entry</Hero>
    </>
  );

  // return (
  //   <>
  //     <Hero>Attendance Record Entry</Hero>
  //
  //     {/* Show this section only if there are no active records */}
  //     {!state.request.records && state.records.length === 0 && (
  //       <div>
  //         {canCreateNextRecord() ? (
  //           <div className={styles.nextRecordForm}>
  //             <p className={styles.contentInfo}>
  //               All time records have been submitted up to the current pay period.<br />
  //               You may enter time for the next pay period by pressing the button below.
  //             </p>
  //             <input
  //               type="button"
  //               className={styles.timeNeutralButton}
  //               title="Create Next Time Record"
  //               value="Create Next Time Record"
  //               onClick={createNextRecord}
  //             />
  //           </div>
  //         ) : (
  //           /*styles margin 10-0*/
  //           <EssNotification level={"info"} title={"No time records available to enter."}>
  //             <p>Please contact Senate Personnel at (518) 455-3376 if you require any assistance.</p>
  //           </EssNotification>
  //         )}
  //       </div>
  //     )}
  //
  //     {/*SUBHERO*/}
  //     {state.records.length > 0 && (<RecordSelectionContainer/>)}
  //
  //     {/* Loader indicator */}
  //     {state.request.records && <LoadingIndicator/>}
  //
  //     {/* Warning for previously unsubmitted records */}
  //     {errorTypes.record.prevUnsubmittedRecord && (
  //       // styles = margin top+bottom 20
  //       <EssNotification level={'warn'} title={"Earlier Unsubmitted Records"}>
  //         <p>
  //           This record cannot be submitted until all previous annual salary records are submitted.
  //         </p>
  //       </EssNotification>
  //     )}
  //
  //     {state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED' ||
  //       state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED_PERSONNEL' && (
  //         <EssNotification level={"error"} title={"Time record requires correction"}>
  //           <p style={{ marginTop: '20px' }}>{state.records[state.iSelectedRecord].initialRemarks}</p>
  //         </EssNotification>
  //     )}
  //
  //     {/* Accruals and Time entry for regular/special annual time record entries */}
  //     {!state.request.records && state.records[state.iSelectedRecord].timeEntries && (
  //       <div className={styles.contentContainer}>
  //         <p className={`${styles.contentInfo} ${styles.margin0}`}>
  //           All hours available need approval from appointing authority.
  //         </p>
  //         {state.annualEntries && state.tempEntries && (
  //           //${styles.margin10}`}
  //           <EssNotification level={"warn"} title={"Record with multiple pay types"}>
  //             <p>
  //               There was a change in pay type during the time covered by this record.<br />
  //               Record days have been split into two separate entry tables, one for Regular/Special Annual pay, another for Temporary pay.
  //             </p>
  //           </EssNotification>
  //         )}
  //         <form id="timeRecordForm" method="post" action="">
  //           {/* Annual Entry Form */}
  //           <AnnualEntryForm/>
  //           {/* Temporary Entry Form */}
  //           <TemporaryEntryForm/>
  //           {/* Save Record Container */}
  //           <div className={styles.saveRecordContainer}>
  //             <div className={styles.recordRemarksContainer}>
  //               <label htmlFor="remarks-text-area">Notes / Remarks</label>
  //               <textarea
  //                 id="remarks-text-area"
  //                 className={styles.recordRemarksTextArea}
  //                 tabIndex="1"
  //                 value={state.records[state.iSelectedRecord].remarks}
  //                 onChange={() => setDirty()}
  //               />
  //             </div>
  //             <div className={styles.floatRight}>
  //               <input
  //                 onClick={() => saveRecord(false)}
  //                 className={styles.submitButton}
  //                 type="button"
  //                 value="Save Record"
  //                 disabled={!state.records[state.iSelectedRecord].dirty || !recordValid()}
  //                 tabIndex={state.records[state.iSelectedRecord].dirty && recordValid() ? 1 : -1}
  //               />
  //               <input
  //                 onClick={() => saveRecord(true)}
  //                 className={styles.submitButton}
  //                 type="button"
  //                 value="Submit Record"
  //                 disabled={!recordSubmittable()}
  //                 tabIndex={recordSubmittable() ? 1 : -1}
  //               />
  //             </div>
  //             <div className={styles.clearfix}></div>
  //           </div>
  //         </form>
  //       </div>
  //     )}
  //
  //     {/*  Modals:  */}
  //     {/* save-progress */}
  //     {/* submit-progress */}
  //     {/* post-save */}
  //     {/* post-submit */}
  //     {/* submit-ack */}
  //     {/* expectedhrs-dialog */}
  //     {/* futureenddt-dialog */}
  //     {/* record-modified-dialog */}
  //     {/* unsubmitted-te-warning */}
  //   </>
  // );
}

















