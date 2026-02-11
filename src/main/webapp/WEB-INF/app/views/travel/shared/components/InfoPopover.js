import { Popover, PopoverButton } from "@headlessui/react";
import { EssPopoverPanel } from "app/components/EssPopover";
import { Info } from "lucide-react";
import React from "react";
import { cn } from "app/utils/cn";

/**
 * Displays an info icon which when clicked will display a popover containing {children}.
 * @param label A label for the popover.
 * @param children The JSX to be displayed on click.
 * @returns {JSX.Element}
 * @constructor
 */
export default function InfoPopover({ label, children }) {
  return (
    <Popover>
      <PopoverButton
        as="button"
        type="button"
        className={cn(
          "inline-flex h-4 w-4 items-center justify-center rounded-full p-0",
          "leading-none text-teal-700 transition hover:bg-teal-50",
          "focus-visible:ring-2 focus-visible:ring-teal-600 focus-visible:outline-none",
        )}
        aria-label={`View ${label}`}
      >
        <Info className="h-4 w-4" />
      </PopoverButton>
      <EssPopoverPanel anchor="bottom" className="w-[680px] max-w-3xl p-0">
        <div className="max-h-[70vh] overflow-y-auto p-4">{children}</div>
      </EssPopoverPanel>
    </Popover>
  );
}
