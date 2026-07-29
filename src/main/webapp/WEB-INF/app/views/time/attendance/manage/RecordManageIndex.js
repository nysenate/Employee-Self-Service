import React, { useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Button from "app/components/Button";
import Accordion from "app/components/Accordion";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useSupEmpGroup } from "app/views/time/personnel/useSupEmpGroup";
import RecordDetailsModal from "app/views/time/attendance/history/RecordDetailsModal";
import SupervisorRecordList from "app/views/time/attendance/manage/SupervisorRecordList";
import RecordReviewModal, {
  ApproveSubmitModal,
} from "app/views/time/attendance/manage/RecordReviewModal";
import {
  RecordReminderPostedModal,
  RecordReminderPromptModal,
  RecordReminderSendingModal,
} from "app/views/time/attendance/manage/RecordReminderModals";
import {
  useReviewRecords,
  useSendReminders,
  useSupervisorRecords,
} from "app/views/time/attendance/manage/useSupervisorRecords";
import {
  buildSupervisorEntries,
  groupRecordsByStatus,
  noRecordsNotice,
  recordDateRange,
  withPendingCounts,
} from "app/views/time/attendance/manage/supervisorEntries";

/**
 * The supervisor's queue: every time record under them that is still moving, grouped by where
 * it stands, with the submitted ones ready to approve or send back.
 *
 * Ported from the legacy RecordManageCtrl (assets/js/src/time/record/record-manage-ctrl.js)
 * and WEB-INF/view/template/time/record/manage.jsp.
 */
export default function RecordManageIndex() {
  const { data: user } = useRequireAuthedUser();
  const supEmpGroup = useSupEmpGroup(user?.employeeId);

  const entries = useMemo(
    () =>
      buildSupervisorEntries(
        supEmpGroup.supEmpGroups,
        supEmpGroup.getName,
        user?.employeeId,
      ),
    [supEmpGroup.supEmpGroups, supEmpGroup.getName, user?.employeeId],
  );

  return (
    <AssertPermission permission="time:management-pages">
      <div>
        <Hero>Review Time Records</Hero>
        {supEmpGroup.isPending ? (
          <LoadingIndicator />
        ) : (
          <RecordManage entries={entries} userEmpId={user.employeeId} />
        )}
      </div>
    </AssertPermission>
  );
}

function RecordManage({ entries, userEmpId }) {
  const [iSelSup, setISelSup] = useState(0);

  const selectedEntry = entries[iSelSup];

  /*
   * The user's own records are always loaded: they are what the pending counts in the dropdown
   * are drawn from. An indirect supervisor's records are a second, separate request.
   */
  const userRange = recordDateRange(entries[0] || {});
  const userRecords = useSupervisorRecords(
    userEmpId,
    userRange.from,
    userRange.to,
  );

  const selectedRange = recordDateRange(selectedEntry || {});
  const selectedRecords = useSupervisorRecords(
    selectedEntry?.querySupId,
    selectedRange.from,
    selectedRange.to,
  );

  // Only the user's own entries carry a count, and all of them read the same set of records.
  const labelledEntries = useMemo(() => {
    const byEntry = entries.map((entry) =>
      entry.extendedSup
        ? {}
        : groupRecordsByStatus(entry, userRecords.data, userEmpId),
    );
    return withPendingCounts(entries, byEntry);
  }, [entries, userRecords.data, userEmpId]);

  const recordsByStatus = useMemo(
    () =>
      selectedEntry
        ? groupRecordsByStatus(selectedEntry, selectedRecords.data, userEmpId)
        : {},
    [selectedEntry, selectedRecords.data, userEmpId],
  );

  return (
    <div>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="sup-select">
          View Employees Under&nbsp;
        </label>
        <select
          id="sup-select"
          name="sup-select"
          className="select"
          value={iSelSup}
          onChange={(e) => setISelSup(parseInt(e.target.value))}
        >
          {labelledEntries.map((entry, index) => (
            <option value={index} key={`${entry.supId}-${index}`}>
              {entry.group ? `${entry.group}: ` : ""}
              {entry.dropDownLabel}
            </option>
          ))}
        </select>
      </Controls>

      {selectedRecords.isPending ? (
        <LoadingIndicator />
      ) : (
        <RecordSections
          key={iSelSup}
          entry={selectedEntry}
          recordsByStatus={recordsByStatus}
          userEmpId={userEmpId}
        />
      )}
    </div>
  );
}

function RecordSections({ entry, recordsByStatus, userEmpId }) {
  /** Selected time record ids, kept separately for each section. */
  const [selections, setSelections] = useState({
    SUBMITTED: new Set(),
    DISAPPROVED: new Set(),
    NOT_SUBMITTED: new Set(),
  });

  const [reviewing, setReviewing] = useState(null);
  const [approving, setApproving] = useState(null);
  const [reminding, setReminding] = useState(null);
  const [reminderResults, setReminderResults] = useState(null);
  const [detailsRecord, setDetailsRecord] = useState(null);

  const review = useReviewRecords();
  const sendReminders = useSendReminders();

  const records = (status) => recordsByStatus[status] || [];
  const has = (status) => records(status).length > 0;

  const selectedRecords = (status, omitOwn) =>
    records(status).filter(
      (record) =>
        selections[status].has(record.timeRecordId) &&
        !(omitOwn && record.employeeId === userEmpId),
    );

  const setSelection = (status, next) =>
    setSelections((current) => ({ ...current, [status]: next }));

  const selectAll = (status) =>
    setSelection(
      status,
      new Set(
        records(status)
          // A supervisor may never approve their own timesheet.
          .filter(
            (record) =>
              !(status === "SUBMITTED" && record.employeeId === userEmpId),
          )
          .map((record) => record.timeRecordId),
      ),
    );

  const selectNone = (status) => setSelection(status, new Set());

  const clearSelections = () =>
    setSelections({
      SUBMITTED: new Set(),
      DISAPPROVED: new Set(),
      NOT_SUBMITTED: new Set(),
    });

  const submitReviews = ({ approved, disapproved }) => {
    setReviewing(null);
    clearSelections();
    review.mutate([
      ...approved.map((record) => ({
        timeRecordId: record.timeRecordId,
        action: "submit",
      })),
      ...disapproved.map((record) => ({
        timeRecordId: record.timeRecordId,
        action: "reject",
        remarks: record.remarks,
      })),
    ]);
  };

  const handleSendReminders = () => {
    const toRemind = reminding;
    setReminding(null);
    sendReminders.mutate(toRemind, {
      onSuccess: (reminders) => setReminderResults(reminders),
    });
  };

  return (
    <div>
      {!has("SUBMITTED") && (
        <Notification level="info" {...noRecordsNotice(entry)} />
      )}

      {review.isError && (
        <Notification
          level="error"
          title="Could not submit your review."
          message="Please try again later."
        />
      )}

      <RecordReminderSendingModal isOpen={sendReminders.isPending} />

      {has("SUBMITTED") && (
        <Card className="mt-3">
          <h2 className="px-3 pt-3 text-xl font-semibold text-teal-700">
            T&amp;A Record(s) Needing Approval ({records("SUBMITTED").length})
          </h2>
          <p className="p-3 text-center">
            Select pending records in the table below and click &apos;Review
            Selected Records&apos;
            <br />
            at the bottom to review the record details and either approve or
            reject them.
          </p>
          <SectionControls
            status="SUBMITTED"
            onSelectAll={selectAll}
            onSelectNone={selectNone}
            hasSelection={selections.SUBMITTED.size > 0}
            actions={[
              {
                label: "Approve Selected",
                onPress: () => setApproving(selectedRecords("SUBMITTED", true)),
              },
              {
                label: "Review Selected",
                variant: "secondary",
                onPress: () =>
                  setReviewing({
                    records: selectedRecords("SUBMITTED", true),
                    allowApproval: true,
                  }),
              },
            ]}
          />
          <SupervisorRecordList
            records={records("SUBMITTED")}
            selected={selections.SUBMITTED}
            onSelectedChange={(next) => setSelection("SUBMITTED", next)}
            userEmpId={userEmpId}
          />
          <SectionControls
            status="SUBMITTED"
            onSelectAll={selectAll}
            onSelectNone={selectNone}
            hasSelection={selections.SUBMITTED.size > 0}
            actions={[
              {
                label: "Approve Selected",
                onPress: () => setApproving(selectedRecords("SUBMITTED", true)),
              },
              {
                label: "Review Selected",
                variant: "secondary",
                onPress: () =>
                  setReviewing({
                    records: selectedRecords("SUBMITTED", true),
                    allowApproval: true,
                  }),
              },
            ]}
          />
        </Card>
      )}

      <Accordion
        allowsMultipleExpanded
        defaultExpandedKeys={["DISAPPROVED", "NOT_SUBMITTED"]}
        className="mt-5"
      >
        {has("DISAPPROVED") && (
          <Accordion.Item
            id="DISAPPROVED"
            title={`T&A Records Awaiting Correction By Employee (${records("DISAPPROVED").length})`}
          >
            <Accordion.Panel>
              <p className="p-3 text-center">
                The following records have been rejected and are pending
                correction by the employee.
                <br />
                Once the employee resubmits the record it will appear in the
                &apos;Records Needing Approval&apos; section.
              </p>
              <ActionableSection
                status="DISAPPROVED"
                records={records("DISAPPROVED")}
                selection={selections.DISAPPROVED}
                onSelectedChange={(next) => setSelection("DISAPPROVED", next)}
                onSelectAll={selectAll}
                onSelectNone={selectNone}
                onView={() =>
                  setReviewing({
                    records: selectedRecords("DISAPPROVED"),
                    allowApproval: false,
                  })
                }
                onEmail={() => setReminding(selectedRecords("DISAPPROVED"))}
              />
            </Accordion.Panel>
          </Accordion.Item>
        )}

        {has("NOT_SUBMITTED") && (
          <Accordion.Item
            id="NOT_SUBMITTED"
            title={`T&A Records Not Submitted (${records("NOT_SUBMITTED").length})`}
          >
            <Accordion.Panel>
              <p className="p-3 text-center">
                The records have not yet been submitted by the employee.
              </p>
              <ActionableSection
                status="NOT_SUBMITTED"
                records={records("NOT_SUBMITTED")}
                selection={selections.NOT_SUBMITTED}
                onSelectedChange={(next) => setSelection("NOT_SUBMITTED", next)}
                onSelectAll={selectAll}
                onSelectNone={selectNone}
                onView={() =>
                  setReviewing({
                    records: selectedRecords("NOT_SUBMITTED"),
                    allowApproval: false,
                  })
                }
                onEmail={() => setReminding(selectedRecords("NOT_SUBMITTED"))}
              />
            </Accordion.Panel>
          </Accordion.Item>
        )}
      </Accordion>

      <Accordion className="mt-5" allowsMultipleExpanded>
        {has("APPROVED") && (
          <ReadOnlySection
            status="APPROVED"
            title={`T&A Records Pending Approval By Personnel (${records("APPROVED").length})`}
            description="The following records have been recently approved and are awaiting approval by Personnel."
            records={records("APPROVED")}
            onRecordClick={setDetailsRecord}
          />
        )}

        {has("DISAPPROVED_PERSONNEL") && (
          <ReadOnlySection
            status="DISAPPROVED_PERSONNEL"
            title={`T&A Records Rejected By Personnel Awaiting Employee Correction (${records("DISAPPROVED_PERSONNEL").length})`}
            description="The records have been rejected by Personnel and are awaiting re-submission by the employee."
            records={records("DISAPPROVED_PERSONNEL")}
            onRecordClick={setDetailsRecord}
          />
        )}

        {has("SUBMITTED_PERSONNEL") && (
          <ReadOnlySection
            status="SUBMITTED_PERSONNEL"
            title={`T&A Personnel Rejected Records Pending Approval (${records("SUBMITTED_PERSONNEL").length})`}
            description="The following records have been recently submitted to Personnel by an employee to correct errors detected by Personnel"
            records={records("SUBMITTED_PERSONNEL")}
            onRecordClick={setDetailsRecord}
          />
        )}
      </Accordion>

      <RecordReviewModal
        records={reviewing?.records}
        allowApproval={reviewing?.allowApproval}
        onSubmit={submitReviews}
        onClose={() => setReviewing(null)}
      />

      {/* Approving straight from the list skips the review dialog but not the acknowledgement. */}
      <ApproveSubmitModal
        isOpen={!!approving}
        approved={approving || []}
        onConfirm={() => {
          const toApprove = approving;
          setApproving(null);
          submitReviews({ approved: toApprove, disapproved: [] });
        }}
        onClose={() => setApproving(null)}
      />

      <RecordReminderPromptModal
        records={reminding}
        onConfirm={handleSendReminders}
        onClose={() => setReminding(null)}
      />

      <RecordReminderPostedModal
        reminders={reminderResults}
        onClose={() => setReminderResults(null)}
      />

      <RecordDetailsModal
        record={detailsRecord}
        onClose={() => setDetailsRecord(null)}
      />
    </div>
  );
}

/** The select links and action buttons that sit above and below each actionable table. */
function SectionControls({
  status,
  onSelectAll,
  onSelectNone,
  hasSelection,
  actions,
}) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-3 px-3 py-2">
      <ul className="flex gap-4">
        <li>
          <Button variant="link" onPress={() => onSelectAll(status)}>
            Select All
          </Button>
        </li>
        <li>
          <Button variant="link" onPress={() => onSelectNone(status)}>
            Select None
          </Button>
        </li>
      </ul>
      <div className="flex gap-3">
        {actions.map((action) => (
          <Button
            key={action.label}
            variant={action.variant}
            isDisabled={!hasSelection}
            onPress={action.onPress}
          >
            {action.label}
          </Button>
        ))}
      </div>
    </div>
  );
}

/** A table whose records can be selected, then viewed or emailed about. */
function ActionableSection({
  status,
  records,
  selection,
  onSelectedChange,
  onSelectAll,
  onSelectNone,
  onView,
  onEmail,
}) {
  const actions = [
    { label: "View Selected", onPress: onView },
    { label: "Email Selected", variant: "secondary", onPress: onEmail },
  ];

  return (
    <div>
      <SectionControls
        status={status}
        onSelectAll={onSelectAll}
        onSelectNone={onSelectNone}
        hasSelection={selection.size > 0}
        actions={actions}
      />
      <SupervisorRecordList
        records={records}
        selected={selection}
        onSelectedChange={onSelectedChange}
      />
      <SectionControls
        status={status}
        onSelectAll={onSelectAll}
        onSelectNone={onSelectNone}
        hasSelection={selection.size > 0}
        actions={actions}
      />
    </div>
  );
}

/** A collapsed table of records that are out of the supervisor's hands. */
function ReadOnlySection({
  status,
  title,
  description,
  records,
  onRecordClick,
}) {
  return (
    <Accordion.Item id={status} title={title}>
      <Accordion.Panel>
        <p className="p-3 text-center">{description}</p>
        <SupervisorRecordList records={records} onRecordClick={onRecordClick} />
      </Accordion.Panel>
    </Accordion.Item>
  );
}
