import React from "react";
import AddressAutocomplete from "app/views/travel/shared/components/AddressAutocomplete";

export default function RouteSegmentFields({
  index,
  leg,
  errors,
  modes,
  onChange,
  onDestinationSelect,
  isDisabled = false,
}) {
  const fieldId = (field) => `segment-${index}-${field}`;

  return (
    <div className="grid gap-4 md:grid-cols-2">
      <AddressAutocomplete
        id={fieldId("from")}
        label="Origin"
        value={leg.from?.addressText ?? ""}
        error={errors[fieldId("from")]}
        onTextChange={(addressText) =>
          onChange({ from: { address: null, addressText } })
        }
        onSelect={(address) =>
          onChange({
            from: {
              address,
              addressText: address.formattedAddressWithCounty,
            },
          })
        }
        isDisabled={isDisabled}
      />
      <Field
        label="Travel date"
        id={fieldId("date")}
        error={errors[fieldId("date")]}
      >
        <input
          id={fieldId("date")}
          type="date"
          className={`input w-full sm:max-w-40 ${errors[fieldId("date")] ? "input--invalid" : ""}`}
          value={toNativeDateValue(leg.travelDate)}
          aria-invalid={Boolean(errors[fieldId("date")])}
          disabled={isDisabled}
          aria-describedby={
            errors[fieldId("date")] ? `${fieldId("date")}-error` : undefined
          }
          onChange={(event) =>
            onChange({ travelDate: fromNativeDateValue(event.target.value) })
          }
        />
      </Field>
      <AddressAutocomplete
        id={fieldId("to")}
        label="Destination"
        value={leg.to?.addressText ?? ""}
        error={errors[fieldId("to")]}
        onTextChange={(addressText) =>
          onChange({ to: { address: null, addressText } })
        }
        onSelect={(address) => {
          onChange({
            to: {
              address,
              addressText: address.formattedAddressWithCounty,
            },
          });
          onDestinationSelect(address);
        }}
        isDisabled={isDisabled}
      />
      <Field
        label="Mode of transportation"
        id={fieldId("mode")}
        error={errors[fieldId("mode")]}
      >
        <select
          id={fieldId("mode")}
          className={`select w-full sm:max-w-64 ${errors[fieldId("mode")] ? "input--invalid" : ""}`}
          value={leg.methodOfTravelDisplayName ?? ""}
          aria-invalid={Boolean(errors[fieldId("mode")])}
          disabled={isDisabled}
          aria-describedby={
            errors[fieldId("mode")] ? `${fieldId("mode")}-error` : undefined
          }
          onChange={(event) =>
            onChange({
              methodOfTravelDisplayName: event.target.value,
              methodOfTravelDescription: "",
            })
          }
        >
          <option value="" disabled hidden aria-label="No mode selected" />
          {modes.map((mode) => (
            <option key={mode.methodOfTravel} value={mode.displayName}>
              {mode.displayName}
            </option>
          ))}
        </select>
      </Field>
      {leg.methodOfTravelDisplayName === "Other" && (
        <Field
          label="Specify mode of transportation"
          id={fieldId("modeOther")}
          error={errors[fieldId("modeOther")]}
          className="md:col-start-2"
        >
          <input
            id={fieldId("modeOther")}
            className={`input w-full ${errors[fieldId("modeOther")] ? "input--invalid" : ""}`}
            value={leg.methodOfTravelDescription ?? ""}
            aria-invalid={Boolean(errors[fieldId("modeOther")])}
            disabled={isDisabled}
            aria-describedby={
              errors[fieldId("modeOther")]
                ? `${fieldId("modeOther")}-error`
                : undefined
            }
            onChange={(event) =>
              onChange({ methodOfTravelDescription: event.target.value })
            }
          />
        </Field>
      )}
    </div>
  );
}

function toNativeDateValue(value) {
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value ?? "");
  if (!match) return "";
  return `${match[3]}-${match[1]}-${match[2]}`;
}

function fromNativeDateValue(value) {
  if (!value) return "";
  const [year, month, day] = value.split("-");
  return `${month}/${day}/${year}`;
}

function Field({ label, id, error, className = "", children }) {
  return (
    <div className={className}>
      <label htmlFor={id} className="mb-1 block font-medium">
        {label}
      </label>
      {children}
      {error && (
        <p id={`${id}-error`} className="mt-1 text-sm font-medium text-red-700">
          {error}
        </p>
      )}
    </div>
  );
}
