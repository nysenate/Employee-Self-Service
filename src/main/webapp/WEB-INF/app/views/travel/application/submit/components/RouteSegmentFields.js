import React from "react";
import AddressAutocomplete from "app/views/travel/shared/components/AddressAutocomplete";
import { isoToMediumDate } from "app/utils/dateUtils";
import { normalizeTravelDate } from "../routeValidation";

export default function RouteSegmentFields({
  index,
  leg,
  dateLabel,
  minTravelDate,
  travelStartDate,
  errors,
  modes,
  onChange,
  onDestinationSelect,
  isDisabled = false,
}) {
  const fieldId = (field) => `segment-${index}-${field}`;
  const dateId = fieldId("date");
  const modeId = fieldId("mode");
  const minDate = toNativeDateValue(normalizeTravelDate(minTravelDate));
  const startDate = toNativeDateValue(normalizeTravelDate(travelStartDate));
  const dateContext = [
    startDate &&
      (!minDate || minDate === startDate) &&
      `Your trip begins ${isoToMediumDate(startDate)}.`,
    minDate &&
      minDate !== startDate &&
      `${index === 0 ? "Return on" : "On"} or after ${isoToMediumDate(minDate)}.`,
  ]
    .filter(Boolean)
    .join(" ");

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
      <Field label={dateLabel} id={dateId} error={errors[dateId]}>
        <input
          id={dateId}
          type="date"
          className={`input w-full sm:max-w-40 ${errors[dateId] ? "input--invalid" : ""}`}
          value={toNativeDateValue(leg.travelDate)}
          min={minDate || undefined}
          aria-invalid={Boolean(errors[dateId])}
          disabled={isDisabled}
          aria-describedby={
            [
              dateContext && `${dateId}-context`,
              errors[dateId] && `${dateId}-error`,
            ]
              .filter(Boolean)
              .join(" ") || undefined
          }
          onChange={(event) =>
            onChange({ travelDate: fromNativeDateValue(event.target.value) })
          }
        />
        {dateContext && (
          <p id={`${dateId}-context`} className="mt-1 text-sm text-gray-600">
            {dateContext}
          </p>
        )}
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
      <Field label="Mode of transportation" id={modeId} error={errors[modeId]}>
        <select
          id={modeId}
          className={`select w-full sm:max-w-40 ${errors[modeId] ? "input--invalid" : ""}`}
          value={leg.methodOfTravelDisplayName ?? ""}
          aria-invalid={Boolean(errors[modeId])}
          disabled={isDisabled}
          aria-describedby={errors[modeId] ? `${modeId}-error` : undefined}
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
      <OtherModeField
        id={fieldId("modeOther")}
        leg={leg}
        errors={errors}
        isDisabled={isDisabled}
        onChange={onChange}
      />
    </div>
  );
}

function OtherModeField({ id, leg, errors, isDisabled, onChange }) {
  if (leg.methodOfTravelDisplayName !== "Other") return null;
  const error = errors[id];
  return (
    <Field
      label="Specify mode of transportation"
      id={id}
      error={error}
      className="md:col-start-2"
    >
      <input
        id={id}
        className={`input w-full ${error ? "input--invalid" : ""}`}
        value={leg.methodOfTravelDescription ?? ""}
        aria-invalid={Boolean(error)}
        disabled={isDisabled}
        aria-describedby={error ? `${id}-error` : undefined}
        onChange={(event) =>
          onChange({ methodOfTravelDescription: event.target.value })
        }
      />
    </Field>
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
