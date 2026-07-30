import React from "react";
import { ArrowDown, ArrowUp, ArrowUpDown, CircleDot } from "lucide-react";
import {
  APPLICATION_HISTORY_SORT_OPTIONS,
  APPLICATION_HISTORY_STATUS_OPTIONS,
} from "app/views/travel/application/history/useApplicationHistorySearchParams";

const STATUS_PRESENTATION = {
  "": {
    leading: <CircleDot className="h-4 w-4 text-gray-500" />,
  },
  DEPARTMENT_HEAD: {
    group: "In review",
    leading: <StatusDot className="bg-orange-500" />,
  },
  TRAVEL_UNIT: {
    group: "In review",
    leading: <StatusDot className="bg-orange-500" />,
  },
  APPROVED: {
    group: "Final",
    leading: <StatusDot className="bg-green-600" />,
  },
  DISAPPROVED: {
    group: "Final",
    leading: <StatusDot className="bg-red-600" />,
  },
  CANCELED: {
    group: "Final",
    leading: <StatusDot className="bg-gray-500" />,
  },
  NOT_APPLICABLE: {
    group: "Final",
    leading: <StatusDot className="bg-gray-500" />,
  },
};

const SORT_PRESENTATION = {
  "startDate:desc": {
    label: "Start date",
    triggerLabel: "Start date · Newest",
    description: "Newest first",
    leading: <ArrowDown className="h-4 w-4 text-gray-500" />,
  },
  "startDate:asc": {
    label: "Start date",
    triggerLabel: "Start date · Oldest",
    description: "Oldest first",
    leading: <ArrowUp className="h-4 w-4 text-gray-500" />,
  },
  "submittedDate:desc": {
    label: "Submitted date",
    triggerLabel: "Submitted · Newest",
    description: "Newest first",
    leading: <ArrowDown className="h-4 w-4 text-gray-500" />,
  },
  "submittedDate:asc": {
    label: "Submitted date",
    triggerLabel: "Submitted · Oldest",
    description: "Oldest first",
    leading: <ArrowUp className="h-4 w-4 text-gray-500" />,
  },
  "status:asc": {
    label: "Status",
    triggerLabel: "Status · A–Z",
    description: "Alphabetical",
    leading: <ArrowUpDown className="h-4 w-4 text-gray-500" />,
  },
};

export const STATUS_FILTER_OPTIONS = addPresentation(
  APPLICATION_HISTORY_STATUS_OPTIONS,
  STATUS_PRESENTATION,
);

export const SORT_FILTER_OPTIONS = addPresentation(
  APPLICATION_HISTORY_SORT_OPTIONS,
  SORT_PRESENTATION,
);

function addPresentation(options, presentationByValue) {
  return options.map((option) => ({
    ...option,
    ...presentationByValue[option.value],
  }));
}

function StatusDot({ className }) {
  return <span className={`h-2.5 w-2.5 rounded-full ${className}`} />;
}
