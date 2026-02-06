"use client"

import * as React from "react"
import * as CheckboxPrimitive from "@radix-ui/react-checkbox"
import { CheckIcon } from "lucide-react"

import { cn } from "app/utils/cn"

/**
 * Checkbox (Radix)
 *
 * This is NOT a native <input type="checkbox">. It wraps Radix Checkbox and
 * therefore uses Radix props/events:
 * - Use `checked` for controlled state.
 * - Use `defaultChecked` for uncontrolled state.
 * - Listen to `onCheckedChange(checked)` instead of DOM `onChange`.
 *
 * Notes:
 * - `checked` can be `true | false | "indeterminate"`. Coerce to boolean if
 *   you only support two states.
 *
 * Example (controlled):
 * <Checkbox
 *   checked={isEnabled}
 *   onCheckedChange={(checked) => setIsEnabled(!!checked)}
 * />
 */
function Checkbox({
  className,
  ...props
}) {
  return (
    <CheckboxPrimitive.Root
      data-slot="checkbox"
      className={cn(
        "peer border-input dark:bg-input/30",
        "data-[state=checked]:bg-teal-700 data-[state=checked]:text-primary-foreground",
        "dark:data-[state=checked]:bg-primary data-[state=checked]:border-teal-700",
        "focus-visible:border-ring focus-visible:ring-ring/50",
        "aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40",
        "aria-invalid:border-destructive size-4 shrink-0 rounded-[4px] border shadow-xs",
        "transition-shadow outline-none focus-visible:ring-[3px]",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className
      )}
      {...props}>
      <CheckboxPrimitive.Indicator
        data-slot="checkbox-indicator"
        className="grid place-content-center text-current transition-none">
        <CheckIcon className="size-3.5" strokeWidth={3}/>
      </CheckboxPrimitive.Indicator>
    </CheckboxPrimitive.Root>
  );
}

export { Checkbox }
