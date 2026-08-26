import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { addDays, format, parseISO } from "date-fns";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import Notification from "app/components/Notification";
import ErrorAlert from "app/components/ErrorAlert";
import LoadingIndicator from "app/components/LoadingIndicator";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  reloadTimeRecordData,
  useActiveTimeRecords,
  useCreateNextRecord,
} from "app/views/time/attendance/useTimeRecords";
import { useQueryClient } from "@tanstack/react-query";
import RecordSelection from "app/views/time/attendance/record-entry/RecordSelection";
import RecordEntryForm from "app/views/time/attendance/record-entry/RecordEntryForm";

/**
 * Attendance record entry.
 * Ported from the legacy RecordEntryController and its entry page
 * (assets/js/src/time/record/record-entry-ctrl.js,
 * WEB-INF/view/template/time/record/entry.jsp).
 */
export default function RecordEntryIndex() {
  const { data: user } = useRequireAuthedUser();
  const empId = user?.employeeId;
  const activeRecords = useActiveTimeRecords(empId);

  // Only employee scoped records are open for entry. The rest are held by a supervisor or
  // personnel, and are only of interest when deciding if the next record may be created.
  const records = (activeRecords.data || []).filter(
    (record) => record.scope === "E",
  );

  const [selectedIndex, setSelectedIndex] = useSelectedRecord(records);
  // Bumped after a save so that entry starts over from the reloaded record.
  const [formVersion, setFormVersion] = useState(0);
  const queryClient = useQueryClient();

  // Only once the employee is done with the save confirmation is the record reloaded, since
  // a submitted record is no longer theirs to enter.
  const handleSaved = () => {
    setFormVersion((version) => version + 1);
    return reloadTimeRecordData(queryClient);
  };

  if (activeRecords.isPending) {
    return (
      <div>
        <Hero>Attendance Record Entry</Hero>
        <LoadingIndicator />
      </div>
    );
  }

  const selectedRecord = records[selectedIndex];

  return (
    <div>
      <Hero>Attendance Record Entry</Hero>

      {records.length === 0 ? (
        <NoActiveRecords empId={empId} allRecords={activeRecords.data} />
      ) : (
        <>
          <RecordSelection
            records={records}
            selectedIndex={selectedIndex}
            onSelect={setSelectedIndex}
          />
          {selectedRecord && (
            <RecordEntryForm
              key={`${selectedRecord.timeRecordId}:${formVersion}`}
              record={selectedRecord}
              records={records}
              empId={empId}
              onSaved={handleSaved}
            />
          )}
        </>
      )}
    </div>
  );
}

/**
 * Keeps the selected record in sync with the "record" search param, which holds the record's
 * begin date. This is what makes a record linkable from the attendance history page.
 */
function useSelectedRecord(records) {
  const [searchParams, setSearchParams] = useSearchParams();

  const paramIndex = records.findIndex(
    (record) => record.beginDate === searchParams.get("record"),
  );
  const selectedIndex = paramIndex >= 0 ? paramIndex : 0;
  const selectedBeginDate = records[selectedIndex]?.beginDate;

  const setSelectedIndex = (index) => {
    setSearchParams(
      (params) => {
        params.set("record", records[index].beginDate);
        return params;
      },
      { replace: true },
    );
  };

  // The param is filled in for the record that ends up selected, so that the address bar
  // always names the record being entered.
  useEffect(() => {
    if (selectedBeginDate && searchParams.get("record") !== selectedBeginDate) {
      setSearchParams(
        (params) => {
          params.set("record", selectedBeginDate);
          return params;
        },
        { replace: true },
      );
    }
  }, [selectedBeginDate, searchParams, setSearchParams]);

  return [selectedIndex, setSelectedIndex];
}

/**
 * Shown when the employee has nothing to enter time against. They may be able to get ahead by
 * creating the record for the next pay period themselves.
 */
function NoActiveRecords({ empId, allRecords }) {
  const createNextRecord = useCreateNextRecord();
  const latestRecord = getLatestRecord(allRecords);

  if (!canCreateNextRecord(allRecords, latestRecord)) {
    return (
      <Notification
        level="info"
        className="my-3"
        title="No time records available to enter."
        message="Please contact Senate Personnel at (518) 455-3376 if you require any assistance."
      />
    );
  }

  const nextRecordDate = format(
    addDays(parseISO(latestRecord.endDate), 1),
    "yyyy-MM-dd",
  );

  return (
    <div className="mt-3 bg-white p-3 text-center">
      <p className="text-center">
        All time records have been submitted up to the current pay period.
        <br />
        You may enter time for the next pay period by pressing the button below.
      </p>
      {createNextRecord.isError && (
        <ErrorAlert
          className="mt-3 text-left"
          title="The next time record could not be created."
        >
          {createNextRecord.error.data?.message}
        </ErrorAlert>
      )}
      <Button
        className="mt-3"
        variant="theme"
        isPending={createNextRecord.isPending}
        onPress={() => createNextRecord.mutate({ empId, date: nextRecordDate })}
      >
        Create Next Time Record
      </Button>
    </div>
  );
}

/** The record covering the latest pay period, whoever currently holds it. */
function getLatestRecord(allRecords) {
  return (allRecords || []).reduce(
    (latest, record) =>
      !latest || record.beginDate > latest.beginDate ? record : latest,
    null,
  );
}

/**
 * The next record may only be created once every record up to the current pay period has been
 * submitted. A latest record that does not cover today means the record manager has fallen
 * behind, which creating a record here would not fix.
 */
function canCreateNextRecord(allRecords, latestRecord) {
  const today = format(new Date(), "yyyy-MM-dd");

  if ((allRecords || []).some((record) => today < record.beginDate)) {
    return false;
  }
  return Boolean(latestRecord) && today <= latestRecord.endDate;
}
