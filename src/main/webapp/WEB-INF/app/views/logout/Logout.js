import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useQueryClient } from "@tanstack/react-query";
import logoutUser from "app/views/logout/logoutUser";
import { AUTHED_USER_QUERY_KEY } from "app/hooks/useRequireAuthedUser";

export default function Logout() {
  const queryClient = useQueryClient();

  React.useEffect(() => {
    logoutUser().finally(() => {
      queryClient.removeQueries({ queryKey: AUTHED_USER_QUERY_KEY });
      queryClient.removeQueries(); // clear all cached user-specific data in client.
      window.location.replace("/login");
    });
  }, [queryClient]);

  return <LoadingIndicator />;
}
