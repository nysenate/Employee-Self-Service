import { useRef } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { parseApplicationId } from "app/views/travel/application/workflow/applicationRoutes";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useApplicationEditSession({ appId, operation }) {
  const normalizedAppId = parseApplicationId(appId);
  const sessionKey = `${operation}:${normalizedAppId}`;
  const snapshotRef = useRef({ sessionKey, data: undefined });

  if (snapshotRef.current.sessionKey !== sessionKey) {
    snapshotRef.current = { sessionKey, data: undefined };
  }

  const query = useQuery({
    queryKey: travelQueryKeys.editorSession(normalizedAppId, operation),
    queryFn: async () => {
      const [applicationResponse, draftResponse] = await Promise.all([
        fetchApiJson(`/travel/applications/${normalizedAppId}`),
        fetchApiJson(`/travel/application/edit/${normalizedAppId}`),
      ]);

      return {
        application: applicationResponse.result,
        draft: draftResponse.result,
      };
    },
    enabled: normalizedAppId !== null,
    retry: false,
    staleTime: 0,
    gcTime: 0,
    refetchOnMount: "always",
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
  });

  if (
    snapshotRef.current.data === undefined &&
    query.isSuccess &&
    query.isFetchedAfterMount
  ) {
    snapshotRef.current.data = query.data;
  }

  const data = snapshotRef.current.data;
  const isInitialized = data !== undefined;

  return {
    ...query,
    data,
    isSuccess: isInitialized,
    isPending: !isInitialized && !query.isError,
    isLoading: !isInitialized && query.isLoading,
  };
}
