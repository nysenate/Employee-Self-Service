import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

/**
 * @typedef {Object} LocationListResponse
 * @property {boolean} success
 * @property {string} message
 * @property {string} responseType
 * @property {number} total
 * @property {number} offsetStart
 * @property {number} offsetEnd
 * @property {number} limit
 * @property {Location[]} result
 */

/**
 * @typedef {Object} LocationAddress
 * @property {string} addr1
 * @property {string} addr2
 * @property {string} city
 * @property {string} county
 * @property {string} country
 * @property {string} state
 * @property {string} zip5
 * @property {string} zip4
 * @property {string} formattedAddressWithCounty
 */

/**
 * @typedef {Object} RespCenterHead
 * @property {boolean} active
 * @property {string} code
 * @property {string} shortName
 * @property {string} name
 * @property {string} affiliateCode
 */

/**
 * @typedef {Object} Location
 * @property {string} locId
 * @property {string} code
 * @property {string} locationType
 * @property {string} locationTypeCode
 * @property {LocationAddress} address
 * @property {RespCenterHead} respCenterHead
 * @property {string} locationDescription
 * @property {boolean} active
 */

function getQueryKey() {
  return ["supply", "locations", "list"];
}

/**
 * Fetch all locations.
 *
 * @returns {import("@tanstack/react-query").UseQueryResult<Location[], Error>}
 */
export function useLocations() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/locations`).then((body) => body.result);
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
