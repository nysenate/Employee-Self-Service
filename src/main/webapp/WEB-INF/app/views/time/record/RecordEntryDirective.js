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
import TemporaryEntryForm from "app/views/time/record/TemporaryEntryForm";
import RecordSelectionContainer from "app/views/time/record/RecordSelectionContainer";
import { useRecordEntryCtrl } from "app/views/time/record/record-entry-ctrl";


//Needs:
//    -styling on bottom
//    -Connect AnnualEntryForm
//    -Connect TemporaryEntryForm
export default function RecordEntryDirective() {
  const {
    state,
    setState,
    errorTypes,
    entryValidators,
    accrualTabIndex,
    recordsLoading,
    accrualsLoading,
    allowancesLoading,
    canCreateNextRecord,
    createNextRecord,
    setDirty,
    saveRecord,
    recordValid,
    recordSubmittable,
    getRecordRangeDisplay,
    selRecordHasRaSaErrors,
    isWeekend,
    preValidation,
    getSelectedRecord,
    getMiscLeavePredicate,
    getHolidayHours,
    isHoliday,
  } = useRecordEntryCtrl();
  useEffect(() => {
    console.log(state);
  }, [state]);

  const inlineDisabledStyles = {
    background: 'url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQIW2NkQAKrVq36zwjjgzhhYWGMYAEYB8RmROaABADeOQ8CXl/xfgAAAABJRU5ErkJggg==) repeat',
  };

  // return (
  //   <>
  //     <Hero>Attendance Record Entry</Hero>
  //   </>
  // );

  return (
    <>
      <Hero>Attendance Record Entry</Hero>

      {/* Show this section only if there are no active records */}
      {!recordsLoading && state.records.length === 0 && (
        <div>
          {canCreateNextRecord() ? (
            <div className={styles.nextRecordForm}>
              <p className={styles.contentInfo}>
                All time records have been submitted up to the current pay period.<br />
                You may enter time for the next pay period by pressing the button below.
              </p>
              <input
                type="button"
                className={styles.timeNeutralButton}
                title="Create Next Time Record"
                value="Create Next Time Record"
                onClick={createNextRecord}
              />
            </div>
          ) : (
            /*styles margin 10-0*/
            <EssNotification level={"info"} title={"No time records available to enter."}>
              <p>Please contact Senate Personnel at (518) 455-3376 if you require any assistance.</p>
            </EssNotification>
          )}
        </div>
      )}

      {/*SUBHERO*/}
      {!recordsLoading && state.records.length > 0 && (<RecordSelectionContainer state={state} getRecordRangeDisplay={getRecordRangeDisplay}/>)}

      {/* Loader indicator */}
      {recordsLoading && <LoadingIndicator/>}

      {/* Warning for previously unsubmitted records */}
      {errorTypes.record.prevUnsubmittedRecord && (
        // styles = margin top+bottom 20
        <EssNotification level={"warn"} title={"Earlier Unsubmitted Records"}>
          <p>
            This record cannot be submitted until all previous annual salary records are submitted.
          </p>
        </EssNotification>
      )}

      {!recordsLoading && state.records[state.iSelectedRecord]?.recordStatus === 'DISAPPROVED' ||
        state.records[state.iSelectedRecord]?.recordStatus === 'DISAPPROVED_PERSONNEL' && (
          <EssNotification level={"error"} title={"Time record requires correction"}>
            <p style={{ marginTop: '20px' }}>{state.records[state.iSelectedRecord].initialRemarks}</p>
          </EssNotification>
      )}

       {/*Accruals and Time entry for regular/special annual time record entries*/}
      {!recordsLoading && state.records[state.iSelectedRecord]?.timeEntries && (
        <div className={styles.contentContainer}>
          <p className={styles.contentInfo} style={{ margin: '0px'}}>
            All hours available need approval from appointing authority.
          </p>
          {state.annualEntries && state.tempEntries && (
            //${styles.margin10}`}
            <EssNotification level={"warn"} title={"Record with multiple pay types"}>
              <p>
                There was a change in pay type during the time covered by this record.<br />
                Record days have been split into two separate entry tables, one for Regular/Special Annual pay, another for Temporary pay.
              </p>
            </EssNotification>
          )}
          <form id="timeRecordForm" method="post" action="">
            {/* Annual Entry Form */}
            {/*<AnnualEntryForm*/}
            {/*  state={state}*/}
            {/*  accrualsLoading={accrualsLoading}*/}
            {/*  errorTypes={errorTypes}*/}
            {/*  entryValidators={entryValidators}*/}
            {/*  accrualTabIndex={accrualTabIndex}*/}
            {/*  setDirty={setDirty}*/}
            {/*  preValidation={preValidation}*/}
            {/*  isWeekend={isWeekend}*/}
            {/*  getSelectedRecord={getSelectedRecord}*/}
            {/*  selRecordHasRaSaErrors={selRecordHasRaSaErrors}*/}
            {/*  getMiscLeavePredicate={getMiscLeavePredicate}*/}
            {/*  getHolidayHours={getHolidayHours}*/}
            {/*  isHoliday={isHoliday}*/}
            {/*/>*/}
            {state.annualEntries && (<div>AnnualEntryForm</div>)}

            {/* Temporary Entry Form */}
            {/*<TemporaryEntryForm*/}
            {/*  state={state}*/}
            {/*  allowancesLoading={allowancesLoading}*/}
            {/*  setDirty={setDirty}*/}
            {/*  entryValidators={entryValidators}*/}
            {/*  preValidation={preValidation}*/}
            {/*/>*/}
            {state.tempEntries && (<div>TemporaryEntryForm</div>)}

            {/* Save Record Container */}
            <div className={styles.saveRecordContainer}>
              <div className={styles.recordRemarksContainer}>
                <label htmlFor="remarks-text-area">Notes / Remarks</label>
                <textarea
                  id="remarks-text-area"
                  className={styles.recordRemarksTextArea}
                  tabIndex="1"
                  value={state.records[state.iSelectedRecord].remarks}
                  onChange={() => setDirty()}
                />
              </div>
              <div style={{ float: 'right'}}>
                <input
                  type="button"
                  className={styles.submitButton}
                  disabled={!state.records[state.iSelectedRecord].dirty || !recordValid()}
                  style={!state.records[state.iSelectedRecord].dirty || !recordValid() ? inlineDisabledStyles : {}}
                  value="Save Record"
                  onClick={() => saveRecord(false)}
                  tabIndex={state.records[state.iSelectedRecord].dirty && recordValid() ? 1 : -1}
                />
                <input
                  type="button"
                  className={styles.submitButton}
                  disabled={!recordSubmittable()}
                  style={!state.records[state.iSelectedRecord].dirty || !recordValid() ? inlineDisabledStyles : {}}
                  value="Submit Record"
                  onClick={() => saveRecord(true)}
                  tabIndex={recordSubmittable() ? 1 : -1}
                />
              </div>
              <div className={styles.clearfix}></div>
            </div>
          </form>
        </div>
      )}

      {/*  Modals/Popups:  */}
      {/* save-progress */}
      {/* submit-progress */}
      {/* post-save */}
      {/* post-submit */}
      {/* submit-ack */}
      {/* expectedhrs-dialog */}
      {/* futureenddt-dialog */}
      {/* record-modified-dialog */}
      {/* unsubmitted-te-warning */}
    </>
  );
}

















