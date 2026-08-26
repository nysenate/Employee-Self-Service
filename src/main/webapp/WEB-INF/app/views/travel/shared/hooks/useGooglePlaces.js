import { useEffect, useState } from "react";
import { useConfig } from "app/hooks/useConfig";

let googlePlacesPromise;

export function useGooglePlaces() {
  const { data: config, isSuccess: configLoaded } = useConfig();
  const [state, setState] = useState(() => ({
    isReady: Boolean(window.google?.maps?.places?.Autocomplete),
    isError: false,
  }));

  useEffect(() => {
    if (state.isReady || !configLoaded) return undefined;
    if (!config?.googleApiKey) {
      setState({ isReady: false, isError: true });
      return undefined;
    }

    let active = true;
    loadGooglePlaces(config.googleApiKey)
      .then(() => {
        if (active) setState({ isReady: true, isError: false });
      })
      .catch(() => {
        if (active) setState({ isReady: false, isError: true });
      });
    return () => {
      active = false;
    };
  }, [config?.googleApiKey, configLoaded, state.isReady]);

  return state;
}

const GOOGLE_PLACES_LOAD_TIMEOUT_MS = 10000;

export function loadGooglePlaces(apiKey) {
  if (window.google?.maps?.places?.Autocomplete) return Promise.resolve();
  if (googlePlacesPromise) return googlePlacesPromise;

  googlePlacesPromise = new Promise((resolve, reject) => {
    const existingScript = document.querySelector(
      'script[src*="maps.google.com/maps/api/js"], script[src*="maps.googleapis.com/maps/api/js"]',
    );
    const script = existingScript ?? document.createElement("script");
    let settled = false;
    const finish = (callback, value) => {
      if (settled) return;
      settled = true;
      window.clearTimeout(timeoutId);
      script.removeEventListener("load", handleLoad);
      script.removeEventListener("error", handleError);
      callback(value);
    };
    const handleLoad = () => {
      if (window.google?.maps?.places?.Autocomplete) finish(resolve);
      else finish(reject, new Error("Google Places did not initialize."));
    };
    const handleError = () =>
      finish(reject, new Error("Google Places could not be loaded."));
    const timeoutId = window.setTimeout(
      () => finish(reject, new Error("Google Places loading timed out.")),
      GOOGLE_PLACES_LOAD_TIMEOUT_MS,
    );
    script.addEventListener("load", handleLoad);
    script.addEventListener("error", handleError);

    if (!existingScript) {
      script.src = `https://maps.google.com/maps/api/js?libraries=places&key=${encodeURIComponent(apiKey)}`;
      script.async = true;
      script.defer = true;
      document.head.appendChild(script);
    } else {
      queueMicrotask(() => {
        if (window.google?.maps?.places?.Autocomplete) finish(resolve);
      });
    }
  }).catch((error) => {
    googlePlacesPromise = undefined;
    throw error;
  });

  return googlePlacesPromise;
}
