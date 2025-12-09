import React from "react";
import { Badge } from "app/components/ui/badge";
import { cn } from "app/utils/cn";

const STATUS_VARIANTS = {
  pending: "bg-orange-100/60 text-orange-800 border-orange-200",
  approved: "bg-green-100/60 text-green-800 border-green-200",
  disapproved: "bg-red-100 text-red-800 border-red-200",
  draft: "bg-teal-100 text-teal-800 border-teal-200",
  canceled: "bg-gray-100 text-gray-800 border-gray-200",
  notApplicable: "bg-gray-100 text-gray-800 border-gray-200",
  default: "bg-gray-100 text-gray-800 border-gray-200",
};

export default function TravelAppStatusBadge({ status }) {
  if (!status || typeof status !== "object") {
    return null;
  }

  const label =
    (typeof status.label === "string" && status.label) ||
    (typeof status.name === "string" && status.name) ||
    null;

  if (!label) {
    return null;
  }

  const className =
    (status.isPending && STATUS_VARIANTS.pending) ||
    (status.isApproved && STATUS_VARIANTS.approved) ||
    (status.isDisapproved && STATUS_VARIANTS.disapproved) ||
    (status.isDraft && STATUS_VARIANTS.draft) ||
    (status.isCanceled && STATUS_VARIANTS.canceled) ||
    (status.isNotApplicable && STATUS_VARIANTS.notApplicable) ||
    STATUS_VARIANTS.default;

  return (
    <Badge variant="outline" className={cn("rounded-none", className)}>
      {label}
    </Badge>
  );
}
