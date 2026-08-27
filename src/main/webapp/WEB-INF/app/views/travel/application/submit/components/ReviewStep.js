import React, { useEffect, useMemo, useRef, useState } from "react";
import Card from "app/components/Card";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { loadGooglePlaces } from "app/views/travel/shared/hooks/useGooglePlaces";
import { useConfig } from "app/hooks/useConfig";

export default function ReviewStep({
  draft,
  actions,
  loadMaps = loadGoogleMaps,
}) {
  const [mapState, setMapState] = useState("loading");
  const [documentError, setDocumentError] = useState(false);
  const mapRef = useRef(null);
  const configQuery = useConfig();

  useEffect(() => {
    let active = true;
    const request = buildDirectionsRequest(draft);
    if (!request) {
      setMapState("error");
      return undefined;
    }
    if (configQuery.isError) {
      setMapState("error");
      return undefined;
    }
    if (!configQuery.isSuccess) return undefined;

    setMapState("loading");
    loadMaps(configQuery.data?.googleApiKey)
      .then(() => {
        if (!active) return undefined;
        return displayRoute(mapRef.current, request);
      })
      .then(() => {
        if (active) setMapState("ready");
      })
      .catch(() => {
        if (active) setMapState("error");
      });
    return () => {
      active = false;
    };
  }, [
    configQuery.data?.googleApiKey,
    configQuery.isError,
    configQuery.isSuccess,
    draft,
    loadMaps,
  ]);

  async function openAttachment(attachment) {
    setDocumentError(false);
    const openedWindow = window.open("", "_blank");
    if (openedWindow) openedWindow.opener = null;
    try {
      const response = await fetch(
        `/api/v1/travel/applications/attachment/${encodeURIComponent(attachment.filename)}`,
        { cache: "no-store" },
      );
      if (!response.ok) throw new Error("Document request failed");
      const documentUrl = URL.createObjectURL(await response.blob());
      if (openedWindow) openedWindow.location = documentUrl;
      else window.open(documentUrl, "_blank", "noopener,noreferrer");
      window.setTimeout(() => URL.revokeObjectURL(documentUrl), 60_000);
    } catch {
      openedWindow?.close();
      setDocumentError(true);
    }
  }

  const reviewApp = useMemo(
    () => ({
      ...draft,
      activeAmendment: draft.amendment,
      submittedDateTime: draft.submittedDateTime ?? new Date().toISOString(),
    }),
    [draft],
  );

  return (
    <Card>
      <Card.Content className="overflow-x-auto p-5">
        <h1 className="sr-only">Review</h1>
        <TravelAppForm
          app={reviewApp}
          className="p-0"
          onAttachmentOpen={openAttachment}
        />

        {documentError && (
          <p
            role="alert"
            aria-label="Document unavailable"
            className="mx-auto mt-4 max-w-3xl font-medium text-red-700"
          >
            The supporting document could not be opened. You can continue
            reviewing the application or try again.
          </p>
        )}

        <section
          className="mx-auto mt-6 max-w-[816px]"
          aria-labelledby="route-map-title"
        >
          <h2 id="route-map-title" className="text-xl font-semibold">
            Driving Route
          </h2>
          <div className="relative mt-3 min-h-72 w-full bg-gray-100 sm:min-h-96">
            <div
              ref={mapRef}
              aria-label="Outbound travel route map"
              className={mapState === "ready" ? "absolute inset-0" : "hidden"}
            />
            {mapState === "loading" && (
              <p
                role="status"
                className="absolute inset-0 flex items-center justify-center font-medium text-gray-600"
              >
                Loading route map…
              </p>
            )}
            {mapState === "error" && (
              <p
                role="alert"
                aria-label="Route map unavailable"
                className="absolute inset-0 flex items-center justify-center p-6 text-center font-medium text-red-700"
              >
                The outbound route map could not be displayed. The application
                summary and actions remain available.
              </p>
            )}
          </div>
        </section>
      </Card.Content>
      <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4">
        {actions}
      </Card.Footer>
    </Card>
  );
}

export function buildDirectionsRequest(draft) {
  const legs = draft.amendment?.route?.outboundLegs ?? [];
  if (legs.length === 0) return null;
  const origin = addressText(legs[0]?.from?.address);
  const destinations = legs.map((leg) => addressText(leg?.to?.address));
  if (!origin || destinations.some((destination) => !destination)) return null;
  return {
    origin,
    destination: destinations.at(-1),
    waypoints: destinations.slice(0, -1).map((location) => ({ location })),
    travelMode: "DRIVING",
  };
}

function addressText(address) {
  return address?.formattedAddressWithCounty ?? address?.formattedAddress ?? "";
}

async function loadGoogleMaps(apiKey) {
  if (window.google?.maps?.DirectionsService) return;
  if (!apiKey) throw new Error("Google Maps configuration is missing");
  await loadGooglePlaces(apiKey);
  if (!window.google?.maps?.DirectionsService)
    throw new Error("Google Maps did not initialize");
}

function displayRoute(element, request) {
  if (!element || !window.google?.maps) throw new Error("Map unavailable");
  const map = new window.google.maps.Map(element, {
    center: { lat: 42.6680631, lng: -73.8807209 },
    zoom: 9,
  });
  const renderer = new window.google.maps.DirectionsRenderer();
  renderer.setMap(map);
  const directions = new window.google.maps.DirectionsService();
  return new Promise((resolve, reject) => {
    directions.route(request, (result, status) => {
      if (status === "OK") {
        renderer.setDirections(result);
        resolve();
      } else reject(new Error(`Directions request failed: ${status}`));
    });
  });
}
