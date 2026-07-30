import React, { useId, useState } from "react";
import { CalendarDays, Check, ChevronDown } from "lucide-react";
import { format, isValid, parseISO } from "date-fns";
import Button from "app/components/Button";
import { EssPopover } from "app/components/EssPopover";
import {
  createCustomDateRange,
  DATE_RANGE_PRESETS,
  resolveDateRangePreset,
} from "app/utils/dateRangeUtils";
import { cn } from "app/utils/cn";

export default function DateRangeFilter({
  value,
  onChange,
  presets = DATE_RANGE_PRESETS,
  className,
}) {
  const id = useId();
  const labelId = `${id}-label`;
  const triggerId = `${id}-trigger`;
  const fromDateId = `${id}-from`;
  const toDateId = `${id}-to`;
  const [isOpen, setIsOpen] = useState(false);
  const [draftFromDate, setDraftFromDate] = useState(value.fromDate ?? "");
  const [draftToDate, setDraftToDate] = useState(value.toDate ?? "");

  const selectedPreset =
    value.selection.type === "preset"
      ? presets.find((preset) => preset.value === value.selection.preset)
      : null;
  const triggerLabel =
    value.selection.type === "custom"
      ? formatCustomRangeLabel(value.fromDate, value.toDate)
      : (selectedPreset?.label ?? "Date range");
  const isCustomRangeValid =
    Boolean(draftFromDate) &&
    Boolean(draftToDate) &&
    draftFromDate <= draftToDate;

  const handleOpenChange = (open) => {
    if (open) {
      setDraftFromDate(value.fromDate ?? "");
      setDraftToDate(value.toDate ?? "");
    }
    setIsOpen(open);
  };

  const selectPreset = (presetValue) => {
    onChange(resolveDateRangePreset(presetValue, new Date(), presets));
    setIsOpen(false);
  };

  const applyCustomRange = () => {
    if (!isCustomRangeValid) {
      return;
    }

    onChange(createCustomDateRange(draftFromDate, draftToDate));
    setIsOpen(false);
  };

  return (
    <div className={cn("grid w-44 flex-none gap-1", className)}>
      <label id={labelId} className="text-sm font-semibold">
        Date Range
      </label>
      <EssPopover
        isOpen={isOpen}
        onOpenChange={handleOpenChange}
        placement="bottom start"
        offset={4}
        contentClassName="w-[300px] p-3"
        trigger={
          <Button
            id={triggerId}
            variant="secondary"
            aria-labelledby={`${labelId} ${triggerId}`}
            className="w-full overflow-hidden border-gray-300 bg-gray-50 px-2 py-1.5 font-normal text-gray-800 [&>span]:w-full [&>span]:min-w-0"
          >
            <span className="flex w-full min-w-0 items-center gap-2">
              <CalendarDays
                aria-hidden="true"
                className="h-4 w-4 shrink-0 text-gray-500"
              />
              <span
                className="min-w-0 flex-1 truncate text-left"
                title={triggerLabel}
              >
                {triggerLabel}
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
        }
      >
        <div className="flex gap-3">
          <DateInput
            id={fromDateId}
            label="From date"
            value={draftFromDate}
            max={draftToDate}
            onChange={setDraftFromDate}
          />
          <DateInput
            id={toDateId}
            label="To date"
            value={draftToDate}
            min={draftFromDate}
            onChange={setDraftToDate}
          />
        </div>
        <div className="mt-3 flex justify-end">
          <Button
            variant="secondary"
            isDisabled={!isCustomRangeValid}
            onPress={applyCustomRange}
          >
            Apply dates
          </Button>
        </div>

        <div className="my-3 border-t border-gray-200" />
        <div className="mb-1 text-sm font-semibold">Quick ranges</div>
        <div className="grid gap-1">
          {presets.map((preset) => (
            <Button
              key={preset.value}
              variant="quiet"
              onPress={() => selectPreset(preset.value)}
              className={cn(
                "w-full px-3 py-2 font-normal",
                selectedPreset?.value === preset.value &&
                  "bg-teal-100 text-teal-800",
              )}
            >
              <span className="flex w-full items-center justify-between">
                {preset.label}
                {selectedPreset?.value === preset.value && (
                  <Check aria-hidden="true" className="ml-2 h-4 w-4 shrink-0" />
                )}
              </span>
            </Button>
          ))}
        </div>
      </EssPopover>
    </div>
  );
}

function DateInput({ id, label, value, min, max, onChange }) {
  return (
    <div className="grid gap-1">
      <label className="text-sm" htmlFor={id}>
        {label}
      </label>
      <div className="relative">
        <input
          id={id}
          type="date"
          className="input w-32 [&::-webkit-calendar-picker-indicator]:opacity-0"
          value={value}
          min={min}
          max={max}
          onChange={(event) => onChange(event.target.value)}
        />
        <span
          aria-hidden="true"
          className="pointer-events-none absolute inset-y-px right-px z-10 flex w-6 items-center justify-center bg-gray-50 text-gray-500"
        >
          <CalendarDays className="h-4 w-4" />
        </span>
      </div>
    </div>
  );
}

function formatCustomRangeLabel(fromDate, toDate) {
  if (!fromDate || !toDate) {
    return "Custom dates";
  }

  const from = parseISO(fromDate);
  const to = parseISO(toDate);

  if (!isValid(from) || !isValid(to)) {
    return "Custom dates";
  }

  if (from.getFullYear() === to.getFullYear()) {
    return `${format(from, "MMM d")} – ${format(to, "MMM d, yyyy")}`;
  }

  return `${format(from, "MMM d, yyyy")} – ${format(to, "MMM d, yyyy")}`;
}
