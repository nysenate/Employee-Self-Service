import React from "react";
import { cn } from "app/utils/cn";
import {
  DialogTrigger,
  Popover as AriaPopover,
  OverlayArrow,
} from "react-aria-components";

/**
 * Shared popover wrapper.
 *
 * @param {React.ReactElement} trigger A pressable trigger element (typically app/components/Button or button).
 * @param {boolean} [isOpen] Controlled open state.
 * @param {(isOpen: boolean) => void} [onOpenChange] Controlled open-state callback.
 * @param {"top"|"bottom"|"left"|"right"|"bottom start"|"bottom end"|"top start"|"top end"} [placement]
 * @param {number} [offset] Distance in pixels from trigger to popover panel.
 * @param {boolean} [showArrow] Whether to show a small popover arrow.
 * @param {string} [className] Class name for outer positioned popover wrapper.
 * @param {string} [contentClassName] Class name for the inner content container.
 * @param {React.ReactNode} children Popover content.
 * @returns {JSX.Element}
 */
export function EssPopover({
  trigger,
  isOpen,
  onOpenChange,
  placement = "top",
  offset = 8,
  showArrow = false,
  className,
  contentClassName,
  children,
}) {
  return (
    <DialogTrigger
      {...(isOpen != null ? { isOpen } : {})}
      onOpenChange={onOpenChange}
    >
      {trigger}
      <EssPopoverPanel
        placement={placement}
        offset={offset}
        showArrow={showArrow}
        className={className}
        contentClassName={contentClassName}
      >
        {children}
      </EssPopoverPanel>
    </DialogTrigger>
  );
}

function EssPopoverPanel({
  placement = "top",
  offset = 8,
  showArrow = false,
  className,
  contentClassName,
  children,
}) {
  return (
    <AriaPopover
      placement={placement}
      offset={offset}
      className={({ isEntering, isExiting, placement: resolvedPlacement }) =>
        cn(
          "z-50 transition duration-150 ease-out outline-none motion-reduce:transition-none",
          "focus-visible:[&>div]:ring-2 focus-visible:[&>div]:ring-black/20",
          isEntering && "scale-95 opacity-0",
          isExiting && "scale-95 opacity-0",
          isEntering &&
            resolvedPlacement === "top" &&
            "translate-y-1",
          isExiting &&
            resolvedPlacement === "top" &&
            "translate-y-1",
          isEntering &&
            resolvedPlacement === "bottom" &&
            "-translate-y-1",
          isExiting &&
            resolvedPlacement === "bottom" &&
            "-translate-y-1",
          isEntering &&
            resolvedPlacement === "left" &&
            "translate-x-1",
          isExiting &&
            resolvedPlacement === "left" &&
            "translate-x-1",
          isEntering &&
            resolvedPlacement === "right" &&
            "-translate-x-1",
          isExiting &&
            resolvedPlacement === "right" &&
            "-translate-x-1",
          className,
        )
      }
    >
      {showArrow && (
        <OverlayArrow>
          {({ placement: arrowPlacement }) => (
            <svg
              width={12}
              height={12}
              viewBox="0 0 12 12"
              className={cn(
                "block fill-white stroke-black/10 stroke-1 forced-colors:fill-[Canvas] forced-colors:stroke-[ButtonBorder]",
                arrowPlacement === "bottom" && "rotate-180",
                arrowPlacement === "left" && "-rotate-90",
                arrowPlacement === "right" && "rotate-90",
              )}
            >
              <path d="M0 0 L6 6 L12 0" />
            </svg>
          )}
        </OverlayArrow>
      )}
      <div
        className={cn(
          "bg-white p-3 shadow-xl ring-1 ring-black/10 outline-none focus:outline-none focus-visible:outline-none",
          contentClassName,
        )}
      >
        {children}
      </div>
    </AriaPopover>
  );
}
