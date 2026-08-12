import React from "react";
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

/**
 * @typedef {Object} SingleSelectFilterOption
 * @property {import("react").Key} value Unique selection value.
 * @property {string} label Label shown in the option list.
 * @property {string} [triggerLabel] Alternate label shown in the trigger.
 * @property {string} [description] Supporting text shown below the label.
 * @property {React.ReactNode} [leading] Decorative content shown before the label.
 * @property {string} [group] Section heading used to group related options.
 */

/**
 * A controlled single-select filter with optional grouped options.
 *
 * @param {Object} props
 * @param {string} props.label Filter label.
 * @param {import("react").Key | null} props.value Selected option value.
 * @param {(value: import("react").Key | null) => void} props.onChange Selection callback.
 * @param {SingleSelectFilterOption[]} props.options Available options.
 * @param {React.ElementType} props.icon Icon shown in the trigger.
 * @param {string} [props.className] Classes applied to the filter container.
 * @param {"stacked" | "inline"} [props.layout="stacked"] Label and trigger layout.
 * @param {string} [props.triggerClassName] Classes applied to the trigger.
 * @param {string} [props.contentClassName] Classes applied to the popover content.
 * @returns {JSX.Element}
 */
export default function SingleSelectFilter({
  label,
  value,
  onChange,
  options,
  icon,
  className,
  layout = "stacked",
  triggerClassName,
  contentClassName = "w-64 p-2",
}) {
  const selectedOption = options.find((option) => option.value === value);
  const { ungroupedOptions, optionGroups } = groupOptions(options);

  return (
    <Select
      value={value}
      onChange={onChange}
      className={cn(
        layout === "inline"
          ? "flex flex-none items-center gap-2"
          : "grid w-44 flex-none gap-1",
        className,
      )}
    >
      {({ isOpen }) => (
        <>
          <Label className="text-sm font-semibold">{label}</Label>
          <FilterTrigger
            icon={icon}
            valueLabel={selectedOption?.triggerLabel ?? selectedOption?.label}
            isOpen={isOpen}
            className={triggerClassName}
          />
          <EssPopoverPanel
            placement="bottom start"
            offset={4}
            contentClassName={contentClassName}
          >
            <ListBox className="outline-none">
              {ungroupedOptions.map((option, index) => (
                <FilterOption
                  key={option.value}
                  option={option}
                  className={
                    index === ungroupedOptions.length - 1 &&
                    optionGroups.length > 0
                      ? "relative mb-4 after:absolute after:right-0 after:-bottom-2 after:left-0 after:border-b after:border-gray-200"
                      : undefined
                  }
                />
              ))}
              {optionGroups.map(
                ({ label: groupLabel, options: groupOptions }) => (
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
                ),
              )}
            </ListBox>
          </EssPopoverPanel>
        </>
      )}
    </Select>
  );
}

function FilterOption({ option, className }) {
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
          className,
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
