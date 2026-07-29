import React, { useMemo, useState } from "react";
import { addDays, format, parseISO } from "date-fns";
import { useNavigate } from "react-router-dom";
import Button from "app/components/Button";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import { cn } from "app/utils/cn";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useEmployee } from "app/views/useEmployee";
import { useAccruals } from "app/views/time/useAccrual";
import { useHolidaysDuring } from "app/views/time/useHoliday";
import { useMiscLeaveTypeList } from "app/views/time/attendance/useTimeRecords";
import {
  HourSquare,
  HourSquareColumn,
} from "app/views/time/attendance/record-entry/HourSquare";
import { useSaveTimeOffRequest } from "app/views/time/accrual/timeoff/useTimeOffRequests";
import { validateTimeOffRequest } from "app/views/time/accrual/timeoff/timeOffRequestValidation";
import {
  accrualsUsed,
  dayTotal,
  displayHours,
  hours,
  isEditable,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

const HOUR_COLUMNS = [
  { field: "workHours", label: "Work" },
  { field: "vacationHours", label: "Vacation" },
  { field: "personalHours", label: "Personal" },
  { field: "sickEmpHours", label: "Sick Emp" },
  { field: "sickFamHours", label: "Sick Fam" },
];

const EMPTY_DAY = {
  date: null,
  checked: false,
  workHours: null,
  holidayHours: null,
  vacationHours: null,
  personalHours: null,
  sickEmpHours: null,
  sickFamHours: null,
  miscHours: null,
  miscType: null,
  misc2Hours: null,
  miscType2: null,
  totalHours: null,
};

/**
 * A single time off request, either being filled in or being read back.
 *
 * Ported from the legacy timeOffRequestView directive
 * (assets/js/src/time/accrual/time-off-request-view-directive.js).
 *
 * Dates are held as ISO strings rather than the legacy Date objects. The legacy page built its
 * dates in local time and then read them back with toISOString, which shifts the date a day
 * earlier for anyone west of UTC.
 *
 * @param request The request being shown. A new request is an empty one.
 * @param initialMode "input" to open in edit mode, "output" to open read only.
 */
export default function TimeOffRequestForm({ request, initialMode }) {
  const navigate = useNavigate();
  const { data: user } = useRequireAuthedUser();
  const empId = user?.employeeId;

  const [mode, setMode] = useState(initialMode);
  const [days, setDays] = useState(() => request.days || []);
  const [addedComment, setAddedComment] = useState("");
  const [errors, setErrors] = useState([]);

  const employee = useEmployee(empId);
  const supId = employee.data?.supervisorId;

  const today = format(new Date(), "yyyy-MM-dd");
  const accrualQuery = useAccruals(empId, today);
  const miscLeaveTypes = useMiscLeaveTypeList();

  /*
   * Holidays for a year either side of today, wide enough to cover any request being entered,
   * so that a date change reads the schedule from a map instead of asking the server per day.
   */
  const holidays = useHolidaysDuring(
    format(addDays(new Date(), -365), "yyyy-MM-dd"),
    format(addDays(new Date(), 365), "yyyy-MM-dd"),
  );

  const save = useSaveTimeOffRequest();

  // A supervisor reading an employee's request has no accruals of their own at stake.
  const isOwnRequest = !request.employeeId || request.employeeId === empId;
  const accruals = useMemo(() => {
    if (!isOwnRequest || !accrualQuery.data) {
      return { personal: 0, vacation: 0, sick: 0 };
    }
    return {
      personal: hours(accrualQuery.data.personalAvailable),
      vacation: hours(accrualQuery.data.vacationAvailable),
      sick: hours(accrualQuery.data.sickAvailable),
    };
  }, [isOwnRequest, accrualQuery.data]);

  const used = useMemo(() => accrualsUsed(days), [days]);
  const accrualsAfter = {
    personal: accruals.personal - used.personal,
    vacation: accruals.vacation - used.vacation,
    sick: accruals.sick - used.sick,
  };

  const isLoading = accrualQuery.isPending || holidays.isPending;
  const editable = isEditable(request);
  const otherContact = isOwnRequest ? "Supervisor" : "Employee";

  const updateDay = (index, changes) => {
    setDays((current) =>
      current.map((day, i) => {
        if (i !== index) {
          return day;
        }
        const next = { ...day, ...changes };
        // Changing the date re-reads the holiday schedule for the new day.
        if ("date" in changes) {
          next.holidayHours =
            hours(holidays.data?.[changes.date]?.hours) || null;
        }
        return { ...next, totalHours: dayTotal(next) };
      }),
    );
  };

  /** Adds today, or the day after the last one already on the request. */
  const addDay = () => {
    setDays((current) => {
      const last = current[current.length - 1];
      const date = last?.date
        ? format(addDays(parseISO(last.date), 1), "yyyy-MM-dd")
        : format(new Date(), "yyyy-MM-dd");
      const holidayHours = hours(holidays.data?.[date]?.hours) || null;
      const day = { ...EMPTY_DAY, date, holidayHours };
      return [...current, { ...day, totalHours: dayTotal(day) }];
    });
  };

  const deleteSelected = () =>
    setDays((current) => current.filter((day) => !day.checked));

  const buildRequest = (status) => {
    const sorted = [...days]
      .sort((a, b) => (a.date || "").localeCompare(b.date || ""))
      .map(({ checked, datePrint, ...day }) => day);

    /*
     * A fresh comment list is built each time. The legacy version pushed onto the request's own
     * comments, so saving twice recorded the same comment twice.
     */
    const comments = [...(request.comments || [])];
    if (addedComment.trim()) {
      comments.push({ text: addedComment.trim(), authorId: empId });
    }

    return {
      requestId: request.requestId,
      status,
      employeeId: empId,
      supervisorId: supId,
      startDate: sorted[0]?.date,
      endDate: sorted[sorted.length - 1]?.date,
      days: sorted,
      comments,
    };
  };

  const validate = () => {
    const messages = validateTimeOffRequest(days, accruals, holidays.data);
    setErrors(messages);
    return messages.length === 0;
  };

  const handleSave = () => {
    if (!validate()) {
      return;
    }
    save.mutate(buildRequest("SAVED"), {
      onSuccess: (requestId) => {
        setAddedComment("");
        setMode("output");
        // A brand new request has no page of its own until it has been saved.
        if (!request.requestId) {
          navigate(`/time/accrual/time-off-request/${requestId}`, {
            replace: true,
          });
        }
      },
    });
  };

  const handleSubmit = () => {
    if (!validate()) {
      return;
    }
    save.mutate(buildRequest("SUBMITTED"), {
      onSuccess: () => navigate("/time/accrual/time-off-request"),
    });
  };

  if (isLoading) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      {errors.length > 0 && (
        <Notification
          level="error"
          title="Please fix the following errors in your request:"
        >
          {errors.map((message) => (
            <p key={message}>{message}</p>
          ))}
        </Notification>
      )}

      {save.isError && (
        <Notification
          level="error"
          title="Your request could not be saved."
          message="Please try again later."
        />
      )}

      <div className="bg-white">
        <p className="p-3 text-center font-semibold">
          Enter the dates and time for approval by your Time and Attendance
          supervisor.
          <br />
          If the hours are approved you will still have to enter them in the
          time record for that date.
        </p>

        <h2 className="px-3 text-xl font-semibold text-teal-700">
          Review/Submit A Time Off Request
        </h2>

        <AccrualSquares caption="Available Hours" accruals={accruals} />

        <DayTable
          days={days}
          mode={mode}
          editable={editable}
          miscLeaveTypes={miscLeaveTypes.data || []}
          onChange={updateDay}
        />

        {mode === "input" && editable && (
          <div className="flex justify-center gap-3 p-3">
            <Button
              variant="secondary"
              isDisabled={days.every((day) => !day.checked)}
              onPress={deleteSelected}
            >
              Delete Selected
            </Button>
            <Button variant="secondary" onPress={addDay}>
              + Add Another Date
            </Button>
          </div>
        )}

        <AccrualSquares
          caption="Hours After Request"
          accruals={accrualsAfter}
        />

        <Comments
          comments={request.comments}
          empId={empId}
          otherContact={otherContact}
          editing={mode === "input"}
          addedComment={addedComment}
          onAddedCommentChange={setAddedComment}
        />

        <div className="flex justify-center gap-3 p-3">
          {mode === "input" ? (
            <>
              <Button
                variant="secondary"
                isDisabled={days.length === 0}
                isPending={save.isPending}
                onPress={handleSave}
              >
                SAVE
              </Button>
              <Button
                isDisabled={days.length === 0}
                isPending={save.isPending}
                onPress={handleSubmit}
              >
                SUBMIT
              </Button>
            </>
          ) : (
            editable && <Button onPress={() => setMode("input")}>EDIT</Button>
          )}
        </div>
      </div>
    </div>
  );
}

/** The three accrual balances, shown above and below the day table. */
function AccrualSquares({ caption, accruals }) {
  return (
    <div className="px-5 py-2 text-center">
      <HourSquare caption={caption}>
        <div className="flex">
          <HourSquareColumn caption="Personal Hours">
            {accruals.personal}
          </HourSquareColumn>
          <HourSquareColumn caption="Vacation Hours">
            {accruals.vacation}
          </HourSquareColumn>
          <HourSquareColumn caption="Sick Hours">
            {accruals.sick}
          </HourSquareColumn>
        </div>
      </HourSquare>
    </div>
  );
}

const HEAD_CELL = "table__head__cell";
const CELL = "table__cell";

function DayTable({ days, mode, editable, miscLeaveTypes, onChange }) {
  const editing = mode === "input";

  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            {editing && <th className={HEAD_CELL}></th>}
            <th className={HEAD_CELL}>Date</th>
            <th className={`${HEAD_CELL} cell--number`}>Work</th>
            <th className={`${HEAD_CELL} cell--number`}>Holiday</th>
            <th className={`${HEAD_CELL} cell--number`}>Vacation</th>
            <th className={`${HEAD_CELL} cell--number`}>Personal</th>
            <th className={`${HEAD_CELL} cell--number`}>Sick Emp</th>
            <th className={`${HEAD_CELL} cell--number`}>Sick Fam</th>
            <th className={`${HEAD_CELL} cell--number`}>Misc</th>
            <th className={HEAD_CELL}>Misc Type</th>
            <th className={`${HEAD_CELL} cell--number`}>Misc 2</th>
            <th className={HEAD_CELL}>Misc 2 Type</th>
            <th className={`${HEAD_CELL} cell--number`}>Total</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {days.map((day, index) =>
            editing ? (
              <EditableDayRow
                key={index}
                day={day}
                editable={editable}
                miscLeaveTypes={miscLeaveTypes}
                onChange={(changes) => onChange(index, changes)}
              />
            ) : (
              <ReadOnlyDayRow key={index} day={day} />
            ),
          )}
        </tbody>
      </table>
    </div>
  );
}

function EditableDayRow({ day, editable, miscLeaveTypes, onChange }) {
  return (
    <tr className="table__row">
      <td className={CELL}>
        <input
          type="checkbox"
          checked={!!day.checked}
          disabled={!editable}
          aria-label="Select day"
          onChange={(e) => onChange({ checked: e.target.checked })}
        />
      </td>
      <td className={CELL}>
        <input
          type="date"
          className="input w-[150px]"
          value={day.date || ""}
          disabled={!editable}
          aria-label="Date"
          onChange={(e) => onChange({ date: e.target.value || null })}
        />
      </td>
      <HourInput
        field="workHours"
        day={day}
        editable={editable}
        onChange={onChange}
      />
      {/* Holiday hours come from the Senate schedule and are never entered by hand. */}
      <td className={`${CELL} cell--number`}>{day.holidayHours}</td>
      {HOUR_COLUMNS.slice(1).map((column) => (
        <HourInput
          key={column.field}
          field={column.field}
          day={day}
          editable={editable}
          onChange={onChange}
        />
      ))}
      <HourInput
        field="miscHours"
        day={day}
        editable={editable}
        onChange={onChange}
      />
      <MiscTypeSelect
        field="miscType"
        day={day}
        editable={editable}
        miscLeaveTypes={miscLeaveTypes}
        onChange={onChange}
      />
      <HourInput
        field="misc2Hours"
        day={day}
        editable={editable}
        onChange={onChange}
      />
      <MiscTypeSelect
        field="miscType2"
        day={day}
        editable={editable}
        miscLeaveTypes={miscLeaveTypes}
        onChange={onChange}
      />
      <td className={`${CELL} cell--number`}>{day.totalHours}</td>
    </tr>
  );
}

function HourInput({ field, day, editable, onChange }) {
  return (
    <td className={`${CELL} cell--number`}>
      <input
        type="number"
        step="0.5"
        min="0"
        className="input w-[70px]"
        placeholder="--"
        value={day[field] ?? ""}
        disabled={!editable}
        aria-label={field}
        onChange={(e) =>
          onChange({ [field]: e.target.value === "" ? null : +e.target.value })
        }
      />
    </td>
  );
}

function MiscTypeSelect({ field, day, editable, miscLeaveTypes, onChange }) {
  return (
    <td className={CELL}>
      <select
        className="select"
        value={day[field] || ""}
        disabled={!editable}
        aria-label={field}
        onChange={(e) => onChange({ [field]: e.target.value || null })}
      >
        <option value="">Choose Type...</option>
        {miscLeaveTypes.map((miscLeave) => (
          <option value={miscLeave.type} key={miscLeave.type}>
            {miscLeave.shortName}
          </option>
        ))}
      </select>
    </td>
  );
}

function ReadOnlyDayRow({ day }) {
  return (
    <tr className="table__row">
      <td className={`${CELL} whitespace-nowrap`}>
        {day.date ? format(parseISO(day.date), "EEE MMM d yyyy") : ""}
      </td>
      <td className={`${CELL} cell--number`}>{displayHours(day.workHours)}</td>
      <td className={`${CELL} cell--number`}>
        {displayHours(day.holidayHours)}
      </td>
      {HOUR_COLUMNS.slice(1).map((column) => (
        <td key={column.field} className={`${CELL} cell--number`}>
          {displayHours(day[column.field])}
        </td>
      ))}
      <td className={`${CELL} cell--number`}>{displayHours(day.miscHours)}</td>
      <td className={CELL}>{day.miscType || "--"}</td>
      <td className={`${CELL} cell--number`}>{displayHours(day.misc2Hours)}</td>
      <td className={CELL}>{day.miscType2 || "--"}</td>
      <td className={`${CELL} cell--number`}>{day.totalHours}</td>
    </tr>
  );
}

/** The comment thread between the employee and their supervisor. */
function Comments({
  comments = [],
  empId,
  otherContact,
  editing,
  addedComment,
  onAddedCommentChange,
}) {
  if (comments.length === 0 && !editing) {
    return null;
  }

  return (
    <div className="p-3">
      <h3 className="font-semibold text-teal-700">Comments:</h3>

      {comments.length === 0 && editing && (
        <p>
          This is the start of a comment thread between you and your{" "}
          {otherContact}:
        </p>
      )}

      {comments.map((comment, index) => (
        <p key={index} className="my-1">
          <strong>
            {comment.authorId === empId ? "Me" : otherContact}:&emsp;
          </strong>
          {comment.text}
        </p>
      ))}

      {editing && (
        <div className="mt-2">
          <p>
            <strong>Me:</strong>
          </p>
          <textarea
            className={cn("input h-24 w-full max-w-2xl")}
            value={addedComment}
            aria-label="Add a comment"
            onChange={(e) => onAddedCommentChange(e.target.value)}
          />
        </div>
      )}
    </div>
  );
}
