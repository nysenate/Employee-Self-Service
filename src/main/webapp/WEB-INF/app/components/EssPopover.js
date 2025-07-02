import React, { Fragment } from "react";
import { PopoverBackdrop, PopoverPanel } from "@headlessui/react";
import clsx from "clsx";

/**
 * Adds common styling for the PopoverPanel.
 */
export const EssPopoverPanel = ({ anchor = "top", children, className }) => (
  <>
    <PopoverBackdrop className={clsx("fixed inset-0 bg-black/15")} />
    <PopoverPanel
      anchor={{ to: anchor, gap: "8px" }}
      className={clsx("bg-white p-3 shadow-lg ring-1 ring-black/10", className)}
    >
      {children}
    </PopoverPanel>
  </>
);
