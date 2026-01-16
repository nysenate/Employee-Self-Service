import React, { useEffect, useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useUserTravelRoles } from "app/views/travel/shared/hooks/useUserTravelRoles";
import { useReviewQueue } from "app/views/travel/reviewer/queue/useReviewQueue";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "app/components/ui/select";
import LoadingIndicator from "app/components/LoadingIndicator";
import { Label } from "app/components/ui/label";
import ReviewQueueResults from "./ReviewQueueResults";

export default function ReviewQueuePage() {
  const { data: userRoles, isPending: isUserRolesPending } =
    useUserTravelRoles();
  const { data: reviewQueue, isPending: isReviewQueuePending } =
    useReviewQueue();

  const [selectedRole, setSelectedRole] = useState(null);
  const [queue, setQueue] = useState([]);

  const dedupedRoles = useMemo(() => {
    if (!userRoles?.allRoles) return [];

    const seen = new Set();
    return userRoles.allRoles.filter((role) => {
      if (seen.has(role.name)) return false;
      seen.add(role.name);
      return true;
    });
  }, [userRoles]);

  // Set a default selectedRole once data is loaded.
  useEffect(() => {
    if (selectedRole || dedupedRoles.length === 0) return;
    setSelectedRole(dedupedRoles[0]);
  }, [dedupedRoles, selectedRole]);

  // Update the queue whenever selectedRole changes.
  useEffect(() => {
    const queueForRole = reviewQueue?.[selectedRole?.name] ?? [];
    setQueue(queueForRole);
  }, [selectedRole, reviewQueue]);

  const isLoading = isUserRolesPending || isReviewQueuePending || !selectedRole;

  if (isLoading) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Review Travel Applications</Hero>
      <Controls>
        <div className="mb-3 font-semibold">
          The following travel applications require your review.
        </div>
        <RoleSelect
          selectedRole={selectedRole}
          setSelectedRole={setSelectedRole}
          roles={dedupedRoles}
          reviewQueue={reviewQueue}
        />
      </Controls>

      <ReviewQueueResults queue={queue} />
    </div>
  );
}

function RoleSelect({ selectedRole, setSelectedRole, roles, reviewQueue }) {
  const handleChange = (value) => {
    const nextRole = roles.find((role) => role.name === value);
    if (nextRole) {
      setSelectedRole(nextRole);
    }
  };

  const roleBgClass = (() => {
    switch (selectedRole?.name) {
      case "DEPARTMENT_HEAD":
        return "bg-orange-500/40";
      case "TRAVEL_ADMIN":
        return "bg-teal-500/40";
      case "SECRETARY_OF_THE_SENATE":
        return "bg-green-500/40";
      default:
        return "";
    }
  })();

  return (
    <div
      className={`flex items-center justify-center gap-2 py-2 ${roleBgClass}`}
    >
      <Label>Active Role:</Label>
      <Select value={selectedRole?.name} onValueChange={handleChange}>
        <SelectTrigger className="min-w-[16rem]">
          <SelectValue placeholder="Select role" />
        </SelectTrigger>
        <SelectContent>
          {roles.map((role) => {
            const count = reviewQueue?.[role.name]?.length ?? 0;
            const label = `${role.displayName} - (${count}) Pending`;
            return (
              <SelectItem key={role.name} value={role.name}>
                {label}
              </SelectItem>
            );
          })}
        </SelectContent>
      </Select>
    </div>
  );
}
