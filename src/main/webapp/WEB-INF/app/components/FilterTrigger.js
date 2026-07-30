import React from "react";
import { ChevronDown } from "lucide-react";
import Button from "app/components/Button";
import { cn } from "app/utils/cn";

export default function FilterTrigger({
  id,
  labelId,
  icon: Icon,
  valueLabel,
  isOpen,
  className,
}) {
  return (
    <Button
      {...(id ? { id } : {})}
      variant="secondary"
      {...(labelId && id ? { "aria-labelledby": `${labelId} ${id}` } : {})}
      className={cn(
        "w-full overflow-hidden border-gray-300 bg-gray-50 px-2 py-1.5 font-normal text-gray-800 [&>span]:w-full [&>span]:min-w-0",
        className,
      )}
    >
      <span className="flex w-full min-w-0 items-center gap-2">
        <Icon aria-hidden="true" className="h-4 w-4 shrink-0 text-gray-500" />
        <span className="min-w-0 flex-1 truncate text-left" title={valueLabel}>
          {valueLabel}
        </span>
        <ChevronDown
          aria-hidden="true"
          className={cn(
            "ml-auto h-4 w-4 shrink-0 transition-transform",
            isOpen && "rotate-180",
          )}
        />
      </span>
    </Button>
  );
}
