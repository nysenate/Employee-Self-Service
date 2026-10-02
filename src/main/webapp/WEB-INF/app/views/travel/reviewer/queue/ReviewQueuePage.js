import { useLocation } from "react-router-dom";
import React, { useEffect, useMemo, useState } from "react";
import { Users } from "lucide-react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import SingleSelectFilter from "app/components/SingleSelectFilter";
import { cn } from "app/utils/cn";
import { useUserTravelRoles } from "app/views/travel/shared/hooks/useUserTravelRoles";
import { useReviewQueue } from "app/views/travel/reviewer/queue/useReviewQueue";
import LoadingStatus from "app/components/LoadingStatus";
import ErrorAlert from "app/components/ErrorAlert";
import Button from "app/components/Button";
import ReviewQueueResults from "./ReviewQueueResults";

const REVIEW_ROLE_PRIORITY = [
  "DEPARTMENT_HEAD",
  "TRAVEL_ADMIN",
  "SECRETARY_OF_THE_SENATE",
  "MAJORITY_LEADER",
  "DELEGATE",
];

export default function ReviewQueuePage() {
  const location = useLocation();
  const { data: userRoles, isPending: isUserRolesPending } =
    useUserTravelRoles();
  const {
    data: reviewQueue,
    isPending: isReviewQueuePending,
    isError: isReviewQueueError,
    isFetching: isReviewQueueFetching,
    refetch: refetchReviewQueue,
  } = useReviewQueue();

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
    setSelectedRole(
      dedupedRoles.find((role) => role.name === location.state?.reviewRole) ??
        dedupedRoles[0],
    );
  }, [dedupedRoles, selectedRole, location.state?.reviewRole]);

  const hasReviewQueue = reviewQueue !== undefined;
  const isLoading =
    isUserRolesPending ||
    isReviewQueuePending ||
    (hasReviewQueue && !selectedRole);

  if (isLoading) {
    return (
      <LoadingStatus
        message="Loading review queue…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  const canChangeRole = dedupedRoles.length > 1;
  const queue = reviewQueue?.[selectedRole?.name] ?? [];

  return (
    <div>
      <Hero>Review Travel Applications</Hero>
      {isReviewQueueError && (
        <ErrorAlert
          className="mt-5"
          title={
            hasReviewQueue
              ? "Unable to refresh review queue"
              : "Unable to load review queue"
          }
        >
          <p>
            {hasReviewQueue
              ? "The previously loaded applications are still shown. Please try again to get the latest queue."
              : "Please try again to load applications awaiting your review."}
          </p>
          <Button
            className="mt-3"
            onPress={() => refetchReviewQueue()}
            isPending={isReviewQueueFetching}
          >
            Try again
          </Button>
        </ErrorAlert>
      )}
      {hasReviewQueue && (
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
      )}

      {hasReviewQueue && (
        <ReviewQueueResults
          queue={queue}
          roleName={canChangeRole ? selectedRole?.displayName : null}
        />
      )}
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
