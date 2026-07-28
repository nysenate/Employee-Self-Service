import React from "react";
import { ChevronDown } from "lucide-react";
import { cn } from "app/utils/cn";
import {
  Button as AriaButton,
  ComboBox as AriaComboBox,
  FieldError,
  Input,
  Label,
  ListBox,
  ListBoxItem,
  Popover,
  Text,
  composeRenderProps,
} from "react-aria-components";

/**
 * @typedef {Object} ComboBoxOption
 * @property {import("react").Key} key Stable unique identifier.
 * @property {string} textValue Text shown in the input after selection.
 * @property {React.ReactNode} [optionLabel] Primary content line in the dropdown.
 * @property {React.ReactNode} [optionDescription] Optional muted secondary line in the dropdown.
 * @property {React.ReactNode} [optionContent] Optional full custom dropdown content override.
 * @property {unknown} data Original unmodified object associated with this option.
 * @property {string} searchText Searchable text used for filtering.
 * @property {boolean} isDisabled Whether this option is disabled.
 */

/**
 * Factory for the strict ComboBox option contract.
 *
 * @param {Object} params
 * @param {import("react").Key} params.key Stable unique identifier.
 * @param {unknown} params.textValue Text shown in the input after selection (stringified + trimmed).
 * @param {React.ReactNode} [params.optionLabel] Primary content line in the dropdown. Defaults to `textValue`.
 * @param {React.ReactNode} [params.optionDescription] Optional muted secondary line in the dropdown.
 * @param {React.ReactNode} [params.optionContent] Optional full custom dropdown content override.
 * @param {unknown} params.data Original unmodified object associated with this option.
 * @param {string} [params.searchText] Optional searchable text. Defaults to `textValue`.
 * @param {boolean} [params.isDisabled=false] Whether this option is disabled.
 * @returns {ComboBoxOption}
 */
export function createComboBoxOption({
  key,
  textValue,
  optionLabel,
  optionDescription,
  optionContent,
  data,
  searchText,
  isDisabled = false,
}) {
  const normalizedTextValue = String(textValue ?? "").trim();
  const normalizedSearchText = String(searchText ?? normalizedTextValue);

  if (process.env.NODE_ENV !== "production") {
    if (key == null) {
      throw new Error('[ComboBox] Option "key" is required.');
    }
    if (!normalizedTextValue) {
      throw new Error('[ComboBox] Option "textValue" is required.');
    }
    // data is required by contract; if you want to allow "no data", pass null explicitly.
    if (arguments[0] && !("data" in arguments[0])) {
      throw new Error(
        '[ComboBox] Option "data" is required (pass null if needed).',
      );
    }
    if (optionContent == null && optionLabel == null) {
      // This will be defaulted below, but keep the contract explicit in dev.
      // If you prefer callers not to pass optionLabel, remove this check.
    }
  }

  const resolvedOptionLabel = optionLabel ?? normalizedTextValue;

  return {
    key,
    textValue: normalizedTextValue,
    optionLabel: resolvedOptionLabel,
    optionDescription,
    optionContent,
    data,
    searchText: normalizedSearchText,
    isDisabled: Boolean(isDisabled),
  };
}

/**
 * Opinionated React Aria combobox primitive for ESS.
 *
 * Required usage contract:
 * - Pass `options` created by `createComboBoxOption`.
 * - Control selection with `selectedKey`.
 * - Handle `onSelectionChange({ key, option })`.
 *
 * @param {Object} props
 * @param {React.ReactNode} [props.label] Visible field label.
 * @param {string} [props.ariaLabel] Accessible name when no visible label is rendered.
 * @param {import("react").Key | null} props.selectedKey Controlled selected key.
 * @param {(selection: { key: import("react").Key | null, option: ComboBoxOption | null }) => void} props.onSelectionChange
 * Called when selection changes.
 * @param {ComboBoxOption[]} props.options Option list created by `createComboBoxOption`.
 * @param {React.ReactNode} [props.description] Helper text shown under the input.
 * @param {React.ReactNode | ((validation: import("react-aria-components").ValidationResult) => React.ReactNode)} [props.errorMessage]
 * Validation message content.
 * @param {string} [props.className] Optional root className.
 * @param {import("react-aria-components").ComboBoxProps<ComboBoxOption> & { placeholder?: string }} [props.passThroughProps]
 * Pass-through React Aria ComboBox props. Common usage includes:
 * `isDisabled`, `isReadOnly`, `isRequired`, `id`, `name`, and `placeholder`.
 * @returns {JSX.Element}
 */
export default function ComboBox({
  label,
  ariaLabel,
  selectedKey,
  onSelectionChange,
  options,
  description,
  errorMessage,
  className,
  ...passThroughProps
}) {
  const [query, setQuery] = React.useState("");
  const { placeholder, ...comboBoxProps } = passThroughProps;

  const optionsByKey = React.useMemo(() => {
    const map = new Map();
    for (const option of options ?? []) {
      map.set(option.key, option);
    }
    return map;
  }, [options]);

  const selectedOption =
    selectedKey == null ? null : (optionsByKey.get(selectedKey) ?? null);

  const filteredOptions = React.useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return options ?? [];
    return (options ?? []).filter((option) =>
      String(option.searchText ?? "")
        .toLowerCase()
        .includes(q),
    );
  }, [options, query]);

  const handleSelectionChange = (key) => {
    const nextOption = key == null ? null : (optionsByKey.get(key) ?? null);
    onSelectionChange({
      key: nextOption?.key ?? null,
      option: nextOption,
    });
  };

  const handleInputChange = (value) => {
    setQuery(value);

    // Common UX: typing away from the selected value clears selection.
    if (selectedOption && value !== selectedOption.textValue) {
      onSelectionChange({ key: null, option: null });
    }
  };

  // Optional guard: if parent passes a selectedKey that doesn't exist, clear it.
  React.useEffect(() => {
    if (selectedKey != null && !selectedOption) {
      onSelectionChange({ key: null, option: null });
    }
  }, [onSelectionChange, selectedKey, selectedOption]);

  if (process.env.NODE_ENV !== "production") {
    if (!label && !ariaLabel) {
      throw new Error('[ComboBox] Provide either "label" or "ariaLabel".');
    }
    if (!Array.isArray(options)) {
      throw new Error(
        '[ComboBox] "options" must be an array of ComboBoxOption values.',
      );
    }
    if (typeof onSelectionChange !== "function") {
      throw new Error('[ComboBox] "onSelectionChange" callback is required.');
    }
    if (selectedKey === undefined) {
      throw new Error(
        '[ComboBox] "selectedKey" is required (use null when nothing is selected).',
      );
    }
  }

  return (
    <AriaComboBox
      {...comboBoxProps}
      aria-label={ariaLabel}
      items={filteredOptions}
      selectedKey={selectedKey}
      onSelectionChange={handleSelectionChange}
      onInputChange={handleInputChange}
      className={composeRenderProps(className, (inputClass, renderProps) =>
        cn(
          "group flex flex-col gap-1",
          renderProps.isDisabled && "opacity-70",
          inputClass,
        ),
      )}
    >
      {label ? (
        <Label className="text-sm font-semibold text-gray-700">{label}</Label>
      ) : null}

      <div className="flex flex-col gap-1">
        <div className="relative">
          <Input
            placeholder={placeholder}
            className={cn(
              "input w-full pr-9 placeholder:text-gray-400",
              "focus-visible:ring-2 focus-visible:ring-teal-600",
            )}
          />

          <AriaButton
            className={({ isFocusVisible, isHovered, isPressed }) =>
              cn(
                "absolute top-1/2 right-1 inline-flex h-7 w-7 -translate-y-1/2 items-center justify-center rounded text-gray-500",
                isHovered && "bg-gray-100 text-gray-700",
                isPressed && "bg-gray-200 text-gray-800",
                isFocusVisible &&
                  "ring-2 ring-teal-600 ring-offset-1 outline-none",
              )
            }
          >
            <ChevronDown aria-hidden className="h-4 w-4" />
          </AriaButton>
        </div>

        {description ? (
          <Text slot="description" className="text-xs text-gray-500">
            {description}
          </Text>
        ) : null}

        <FieldError className="text-xs font-medium text-red-700">
          {errorMessage}
        </FieldError>

        <Popover
          className={({ isEntering, isExiting }) =>
            cn(
              "z-50 [width:var(--trigger-width)] rounded-md border border-gray-200 bg-white p-1 shadow-xl",
              "transition duration-120 ease-out",
              isEntering && "scale-95 opacity-0",
              isExiting && "scale-95 opacity-0",
            )
          }
        >
          <ListBox className="max-h-64 overflow-auto outline-none">
            {(option) => {
              const hasDescription = option.optionDescription != null;

              return (
                <ListBoxItem
                  id={option.key}
                  textValue={option.textValue}
                  isDisabled={option.isDisabled}
                  className={({ isFocused, isSelected, isDisabled }) =>
                    cn(
                      "cursor-default rounded px-2.5 py-2 text-sm text-gray-900 outline-none",
                      isFocused && "bg-teal-600 text-white",
                      isSelected && "font-semibold",
                      isDisabled && "opacity-50",
                    )
                  }
                >
                  {(renderProps) =>
                    option.optionContent ?? (
                      <div className="flex flex-col">
                        <span>{option.optionLabel}</span>
                        {hasDescription ? (
                          <span
                            className={cn(
                              "text-muted-foreground text-xs",
                              renderProps.isSelected && "text-gray-500",
                              renderProps.isFocused && "text-white/90",
                              renderProps.isDisabled && "text-gray-400",
                            )}
                          >
                            {option.optionDescription}
                          </span>
                        ) : null}
                      </div>
                    )
                  }
                </ListBoxItem>
              );
            }}
          </ListBox>
        </Popover>
      </div>
    </AriaComboBox>
  );
}
