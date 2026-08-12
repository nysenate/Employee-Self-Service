import React, { useEffect, useMemo, useState } from "react";
import { Users } from "lucide-react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import SingleSelectFilter from "app/components/SingleSelectFilter";
import { cn } from "app/utils/cn";
import { useUserTravelRoles } from "app/views/travel/shared/hooks/useUserTravelRoles";
import { useReviewQueue } from "app/views/travel/reviewer/queue/useReviewQueue";
import LoadingIndicator from "app/components/LoadingIndicator";
import ReviewQueueResults from "./ReviewQueueResults";

const REVIEW_ROLE_PRIORITY = [
  "DEPARTMENT_HEAD",
  "TRAVEL_ADMIN",
  "SECRETARY_OF_THE_SENATE",
  "MAJORITY_LEADER",
  "DELEGATE",
];

export default function ReviewQueuePage() {
  const { data: userRoles, isPending: isUserRolesPending } =
    useUserTravelRoles();
  const { data: reviewQueue, isPending: isReviewQueuePending } =
    useReviewQueue();

  const [selectedRole, setSelectedRole] = useState(null);

  const dedupedRoles = useMemo(() => {
    if (!userRoles?.allRoles) return [];

    const seen = new Set();
    return userRoles.allRoles
      .filter((role) => {
        if (seen.has(role.name)) return false;
        seen.add(role.name);
        return true;
      })
      .sort((a, b) => rolePriority(a.name) - rolePriority(b.name));
  }, [userRoles]);

  // Set a default selectedRole once data is loaded.
  useEffect(() => {
    if (selectedRole || dedupedRoles.length === 0) return;
    setSelectedRole(dedupedRoles[0]);
  }, [dedupedRoles, selectedRole]);

  const isLoading = isUserRolesPending || isReviewQueuePending || !selectedRole;

  if (isLoading) {
    return <LoadingIndicator />;
  }

  const canChangeRole = dedupedRoles.length > 1;
  const queue = reviewQueue?.[selectedRole.name] ?? [];

  return (
    <div>
      <Hero>Review Travel Applications</Hero>
      <Controls>
        <div
          className={cn("text-center text-gray-600", canChangeRole && "mb-3")}
        >
          The following travel applications require your review.
        </div>
        {canChangeRole && (
          <div className="my-3 flex justify-center">
            <RoleSelect
              selectedRole={selectedRole}
              setSelectedRole={setSelectedRole}
              roles={dedupedRoles}
              reviewQueue={reviewQueue}
            />
          </div>
        )}
      </Controls>

      <ReviewQueueResults
        queue={queue}
        roleName={canChangeRole ? selectedRole?.displayName : null}
      />
    </div>
  );
}

function rolePriority(roleName) {
  const priority = REVIEW_ROLE_PRIORITY.indexOf(roleName);
  return priority === -1 ? REVIEW_ROLE_PRIORITY.length : priority;
}

function RoleSelect({ selectedRole, setSelectedRole, roles, reviewQueue }) {
  const handleChange = (value) => {
    const nextRole = roles.find((role) => role.name === value);
    if (nextRole) {
      setSelectedRole(nextRole);
    }
  };

  const options = roles.map((role) => {
    const count = reviewQueue?.[role.name]?.length ?? 0;
    return {
      value: role.name,
      label: role.displayName,
      description: `${count} pending`,
    };
  });

  return (
    <SingleSelectFilter
      label="Reviewing as"
      value={selectedRole?.name ?? ""}
      options={options}
      icon={Users}
      layout="inline"
      triggerClassName="w-64"
      onChange={handleChange}
    />
  );
}
