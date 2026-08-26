import React, { useEffect, useRef } from "react";
import { useGooglePlaces } from "../hooks/useGooglePlaces";

function component(components, type, short = false) {
  const value = components?.find((item) => item.types.includes(type));
  return short ? value?.short_name : value?.long_name;
}

export function placeToTravelAddress(place) {
  const parts = place?.address_components;
  if (!parts) return null;
  const streetNumber = component(parts, "street_number");
  const route = component(parts, "route");
  const zip5 = component(parts, "postal_code");
  return {
    placeId: place.place_id ?? "",
    name: place.name ?? "",
    addr1: [streetNumber, route].filter(Boolean).join(" "),
    city:
      component(parts, "locality") ??
      component(parts, "postal_town") ??
      component(parts, "administrative_area_level_2"),
    state: component(parts, "administrative_area_level_1", true) ?? "",
    zip5: zip5 ?? "",
    county: (component(parts, "administrative_area_level_2") ?? "").replace(
      / County$/,
      "",
    ),
    country: component(parts, "country") ?? "",
    formattedAddressWithCounty: place.formatted_address ?? "",
  };
}

export default function AddressAutocomplete({
  id,
  label,
  value,
  error,
  onTextChange,
  onSelect,
}) {
  const inputRef = useRef(null);
  const onSelectRef = useRef(onSelect);
  const { isReady, isError } = useGooglePlaces();

  useEffect(() => {
    onSelectRef.current = onSelect;
  }, [onSelect]);

  useEffect(() => {
    const Autocomplete = window.google?.maps?.places?.Autocomplete;
    if (!isReady || !Autocomplete || !inputRef.current) return undefined;
    const autocomplete = new Autocomplete(inputRef.current, {
      fields: ["address_components", "formatted_address", "name", "place_id"],
      types: ["address"],
    });
    const listener = autocomplete.addListener("place_changed", () => {
      const address = placeToTravelAddress(autocomplete.getPlace());
      if (address) onSelectRef.current(address);
    });
    return () => listener?.remove?.();
  }, [isReady]);

  return (
    <div>
      <label htmlFor={id} className="mb-1 block font-medium">
        {label}
      </label>
      <input
        ref={inputRef}
        id={id}
        className={`input w-full ${error ? "input--invalid" : ""}`}
        value={value}
        placeholder={`${label} address`}
        autoComplete="off"
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : undefined}
        onChange={(event) => onTextChange(event.target.value)}
      />
      {isError && (
        <p role="alert" className="mt-1 text-sm font-medium text-red-700">
          Address suggestions could not be loaded. Refresh the page to try
          again.
        </p>
      )}
      {error && (
        <p id={`${id}-error`} className="mt-1 text-sm font-medium text-red-700">
          {error}
        </p>
      )}
    </div>
  );
}
