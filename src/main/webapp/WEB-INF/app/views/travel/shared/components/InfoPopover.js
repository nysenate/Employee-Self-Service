import { EssPopover } from "app/components/EssPopover";
import { Info } from "lucide-react";
import React from "react";
import { cn } from "app/utils/cn";
import Button from "app/components/Button";

/**
 * Displays an info icon which when clicked will display a popover containing {children}.
 * @param label A label for the popover.
 * @param children The JSX to be displayed on click.
 * @returns {JSX.Element}
 * @constructor
 */
export default function InfoPopover({ label, children }) {
  return (
    <EssPopover
      placement="bottom"
      trigger={
        <Button
          variant="quiet"
          aria-label={`View ${label}`}
          aria-haspopup="dialog"
          className={cn(
            "h-4 w-4 rounded-full p-0 text-teal-700 hover:bg-teal-50",
            "focus-visible:ring-teal-600",
          )}
        >
          <Info className="h-4 w-4" />
        </Button>
      }
      className="w-[680px] max-w-3xl"
      contentClassName="p-0"
    >
      <div className="max-h-[70vh] overflow-y-auto p-4">{children}</div>
    </EssPopover>
  );
}
