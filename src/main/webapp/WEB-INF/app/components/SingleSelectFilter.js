import React, { useMemo, useState } from "react";
import { Check } from "lucide-react";
import {
  Header,
  Label,
  ListBox,
  ListBoxItem,
  ListBoxSection,
  Select,
} from "react-aria-components";
import { EssPopoverPanel } from "app/components/EssPopover";
import FilterTrigger from "app/components/FilterTrigger";
import { cn } from "app/utils/cn";

export default function SingleSelectFilter({
  label,
  value,
  onChange,
  options,
  icon,
  className,
  contentClassName = "w-64 p-2",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const selectedOption = options.find((option) => option.value === value);
  const { ungroupedOptions, optionGroups } = useMemo(
    () => groupOptions(options),
    [options],
  );

  const selectOption = (nextValue) => {
    onChange(nextValue);
  };

  return (
    <Select
      selectedKey={value}
      onSelectionChange={selectOption}
      isOpen={isOpen}
      onOpenChange={setIsOpen}
      className={cn("grid w-44 flex-none gap-1", className)}
    >
      <Label className="text-sm font-semibold">{label}</Label>
      <FilterTrigger
        icon={icon}
        valueLabel={selectedOption?.triggerLabel ?? selectedOption?.label}
        isOpen={isOpen}
      />
      <EssPopoverPanel
        placement="bottom start"
        offset={4}
        contentClassName={contentClassName}
      >
        <ListBox className="outline-none">
          {ungroupedOptions.length > 0 && (
            <ListBoxSection
              className={cn(
                "outline-none",
                optionGroups.length > 0 && "mb-2 border-b border-gray-200 pb-2",
              )}
            >
              {ungroupedOptions.map((option) => (
                <FilterOption key={option.value} option={option} />
              ))}
            </ListBoxSection>
          )}
          {optionGroups.map(({ label: groupLabel, options: groupOptions }) => (
            <ListBoxSection
              key={groupLabel}
              className="outline-none not-last:mb-2"
            >
              <Header className="px-3 pt-1 pb-1 text-xs font-semibold tracking-wide text-gray-500 uppercase">
                {groupLabel}
              </Header>
              {groupOptions.map((option) => (
                <FilterOption key={option.value} option={option} />
              ))}
            </ListBoxSection>
          ))}
        </ListBox>
      </EssPopoverPanel>
    </Select>
  );
}

function FilterOption({ option }) {
  return (
    <ListBoxItem
      id={option.value}
      textValue={option.label}
      className={({ isSelected, isFocused, isPressed, isFocusVisible }) =>
        cn(
          "flex w-full cursor-default items-center gap-3 px-3 py-2 text-sm text-gray-800 outline-none",
          isSelected && "bg-teal-100 text-teal-800",
          isFocused && !isSelected && "bg-gray-100 text-gray-900",
          isPressed && !isSelected && "bg-gray-200 text-gray-900",
          isFocusVisible && "ring-2 ring-teal-600 ring-inset",
        )
      }
    >
      {({ isSelected }) => (
        <>
          {option.leading && (
            <span
              aria-hidden="true"
              className="flex h-5 w-5 shrink-0 items-center justify-center"
            >
              {option.leading}
            </span>
          )}
          <span className="min-w-0 flex-1">
            <span className="block font-medium">{option.label}</span>
            {option.description && (
              <span
                className={cn(
                  "block text-xs text-gray-500",
                  isSelected && "text-teal-700",
                )}
              >
                {option.description}
              </span>
            )}
          </span>
          <span className="flex h-5 w-5 shrink-0 items-center justify-center">
            {isSelected && <Check aria-hidden="true" className="h-4 w-4" />}
          </span>
        </>
      )}
    </ListBoxItem>
  );
}

function groupOptions(options) {
  const ungroupedOptions = [];
  const groupsByLabel = new Map();

  options.forEach((option) => {
    if (!option.group) {
      ungroupedOptions.push(option);
      return;
    }

    if (!groupsByLabel.has(option.group)) {
      groupsByLabel.set(option.group, []);
    }
    groupsByLabel.get(option.group).push(option);
  });

  return {
    ungroupedOptions,
    optionGroups: Array.from(groupsByLabel, ([label, groupedOptions]) => ({
      label,
      options: groupedOptions,
    })),
  };
}
