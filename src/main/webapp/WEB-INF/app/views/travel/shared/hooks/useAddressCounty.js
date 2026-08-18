import { useMutation } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useAddressCounty() {
  return useMutation({
    mutationFn: (address) =>
      fetchApiJson(
        `/travel/geocode?address=${encodeURIComponent(address.formattedAddressWithCounty)}`,
      ).then(extractCounty),
  });
}

export function extractCounty(body) {
  const components = body.results?.[0]?.address_components ?? [];
  return (
    components
      .find((item) => item.types?.includes("administrative_area_level_2"))
      ?.long_name?.replace(/ County$/, "") ?? ""
  );
}
