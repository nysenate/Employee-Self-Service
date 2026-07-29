import React, { useEffect, useMemo, useState } from "react";
import { format, parseISO } from "date-fns";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import { cn } from "app/utils/cn";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  useSaveSupervisorGrants,
  useSupervisorChain,
  useSupervisorGrants,
  useSupervisorOverrides,
} from "app/views/time/personnel/useSupervisorGrants";

/**
 * Lets a supervisor delegate review and approval of their employees' time records to a
 * supervisor above them, and shows the delegations they have been given in return.
 *
 * Ported from the legacy GrantPrivilegesCtrl (assets/js/src/time/grant/grant-ctrl.js) and
 * WEB-INF/view/template/time/record/grant.jsp.
 */
export default function GrantSupervisorAccessIndex() {
  const { data: user } = useRequireAuthedUser();
  const empId = user?.employeeId;

  const chain = useSupervisorChain(empId);
  const grants = useSupervisorGrants(empId);
  const overrides = useSupervisorOverrides(empId);

  const isLoading = chain.isPending || grants.isPending || overrides.isPending;

  return (
    <div>
      <Hero>Grant Supervisor Access</Hero>

      {isLoading ? (
        <LoadingIndicator />
      ) : (
        <GrantSupervisorAccess
          empId={empId}
          supChain={chain.data}
          grants={grants.data}
          overrides={overrides.data}
        />
      )}
    </div>
  );
}

function GrantSupervisorAccess({ empId, supChain, grants, overrides }) {
  // The saved grants, which the local edits below are reset back to when discarded.
  const savedGrantees = useMemo(
    () => buildGrantees(supChain, grants),
    [supChain, grants],
  );

  const [grantees, setGrantees] = useState(savedGrantees);
  const save = useSaveSupervisorGrants();

  // A successful save refetches the grants, which become the new baseline to discard back to.
  useEffect(() => {
    setGrantees(savedGrantees);
  }, [savedGrantees]);

  const modified = grantees.some((grantee) => grantee.modified);

  const updateGrantee = (empIdOfGrantee, changes) => {
    setGrantees((current) =>
      current.map((grantee) =>
        grantee.employeeId === empIdOfGrantee
          ? { ...grantee, ...changes, modified: true }
          : grantee,
      ),
    );
  };

  const handleSave = () => {
    save.mutate(
      grantees
        .filter((grantee) => grantee.modified)
        .map((grantee) => ({
          granteeSupervisorId: grantee.employeeId,
          granterSupervisorId: empId,
          active: grantee.granted,
          startDate: grantee.grantStart || null,
          endDate: grantee.grantEnd || null,
        })),
    );
  };

  const granters = useMemo(() => buildGranters(overrides), [overrides]);

  return (
    <div>
      {grantees.length === 0 ? (
        <Notification
          level="warn"
          title="No supervisor grants available."
          message="You do not have any supervisors that you can delegate your employee's record approvals to. Please contact Senate Personnel for more information."
        />
      ) : (
        <div className="bg-white">
          <p className="p-3 text-center">
            Grant another supervisor privileges to review and/or approve your
            direct employee&apos;s time records.
          </p>

          <GranteeTable grantees={grantees} onChange={updateGrantee} />

          <hr className="border-gray-300" />

          {save.isError && (
            <Notification
              level="error"
              title="Could not update grants."
              message="Please try again later."
            />
          )}

          {save.isSuccess && !modified && (
            <Notification level="info" title="Grants have been updated." />
          )}

          <Controls className="flex justify-center gap-3">
            <Button
              variant="secondary"
              isDisabled={!modified}
              onPress={() => setGrantees(savedGrantees)}
            >
              Discard Changes
            </Button>
            <Button
              isDisabled={!modified}
              isPending={save.isPending}
              onPress={handleSave}
            >
              Change Supervisor Access
            </Button>
          </Controls>
        </div>
      )}

      {granters.length > 0 && (
        <div className="mt-5 bg-white">
          <p className="p-3 text-center">
            The following employees have granted privileges to you.
          </p>
          <GranterTable granters={granters} />
        </div>
      )}
    </div>
  );
}

const HEAD_CELL = "table__head__cell";
const CELL = "table__cell";

function GranteeTable({ grantees, onChange }) {
  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={HEAD_CELL}>#</th>
            <th className={HEAD_CELL}>Supervisor</th>
            <th className={HEAD_CELL}>Status</th>
            <th className={HEAD_CELL}>Start Date</th>
            <th className={HEAD_CELL}>End Date</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {grantees.map((grantee, index) => (
            <tr className="table__row" key={grantee.employeeId}>
              <td className={CELL}>{index + 1}</td>
              <td className={`${CELL} whitespace-nowrap`}>
                {grantee.firstName} {grantee.lastName}
              </td>
              <td className={CELL}>
                <label className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={grantee.granted}
                    onChange={(e) =>
                      onChange(grantee.employeeId, {
                        granted: e.target.checked,
                      })
                    }
                  />
                  <span
                    className={cn(
                      grantee.granted && "font-semibold text-green-700",
                    )}
                  >
                    Grant Access
                  </span>
                </label>
              </td>
              <DateCell
                label="Set Start Date"
                granted={grantee.granted}
                value={grantee.grantStart}
                max={grantee.grantEnd}
                onChange={(grantStart) =>
                  onChange(grantee.employeeId, { grantStart })
                }
              />
              <DateCell
                label="Set End Date"
                granted={grantee.granted}
                value={grantee.grantEnd}
                min={grantee.grantStart}
                onChange={(grantEnd) =>
                  onChange(grantee.employeeId, { grantEnd })
                }
              />
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/**
 * One end of a grant's date range: a checkbox that opts into bounding the grant, and the date
 * itself. Checking the box defaults to today, as in the legacy page; unchecking clears it so the
 * grant runs open ended in that direction.
 */
function DateCell({ label, granted, value, min, max, onChange }) {
  return (
    <td className={cn(CELL, !granted && "opacity-50")}>
      <div className="flex items-center gap-2">
        <label className="flex items-center gap-2 whitespace-nowrap">
          <input
            type="checkbox"
            disabled={!granted}
            checked={!!value}
            onChange={(e) =>
              onChange(
                e.target.checked ? format(new Date(), "yyyy-MM-dd") : null,
              )
            }
          />
          {label}
        </label>
        <input
          type="date"
          className={cn(
            "input w-[140px]",
            (!granted || !value) && "opacity-50",
          )}
          disabled={!granted || !value}
          value={value || ""}
          min={min || undefined}
          max={max || undefined}
          onChange={(e) => onChange(e.target.value || null)}
        />
      </div>
    </td>
  );
}

function GranterTable({ granters }) {
  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={HEAD_CELL}>#</th>
            <th className={HEAD_CELL}>Supervisor</th>
            <th className={HEAD_CELL}>Status</th>
            <th className={HEAD_CELL}>Start Date</th>
            <th className={HEAD_CELL}>End Date</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {granters.map((granter, index) => (
            <tr className="table__row" key={granter.employeeId}>
              <td className={CELL}>{index + 1}</td>
              <td className={`${CELL} whitespace-nowrap`}>
                {granter.firstName} {granter.lastName}
              </td>
              <td className={CELL}>{granter.status}</td>
              <td className={`${CELL} whitespace-nowrap`}>
                {granter.grantStartStr}
              </td>
              <td className={`${CELL} whitespace-nowrap`}>
                {granter.grantEndStr}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/**
 * The supervisors the user may grant access to: everyone in their supervisor chain, plus anyone
 * already granted access who has since left it. Existing grants fill in the dates.
 */
function buildGrantees(supChain, grants) {
  const grantees = (supChain || []).map((sup) => ({
    ...sup,
    granted: false,
    grantStart: null,
    grantEnd: null,
    modified: false,
  }));
  const byEmpId = new Map(grantees.map((sup) => [sup.employeeId, sup]));

  (grants || []).forEach((grant) => {
    let grantee = byEmpId.get(grant.granteeSupervisorId);
    if (!grantee) {
      grantee = { ...grant.granteeSupervisor, modified: false };
      grantees.push(grantee);
      byEmpId.set(grant.granteeSupervisorId, grantee);
    }
    grantee.granted = true;
    grantee.grantStart = grant.startDate;
    grantee.grantEnd = grant.endDate;
  });

  return grantees;
}

/** The active grants handed to the user, labelled with where each one stands today. */
function buildGranters(overrides) {
  const today = format(new Date(), "yyyy-MM-dd");

  return (overrides || [])
    .filter((override) => override.active)
    .map((override) => {
      let status = "Active";
      if (override.startDate && today < override.startDate) {
        status = "Pending";
      } else if (override.endDate && today > override.endDate) {
        status = "Expired";
      }

      return {
        ...override.overrideSupervisor,
        status,
        grantStartStr: override.startDate
          ? format(parseISO(override.startDate), "MM/dd/yyyy")
          : "No Start Date",
        grantEndStr: override.endDate
          ? format(parseISO(override.endDate), "MM/dd/yyyy")
          : "No End Date",
      };
    });
}
