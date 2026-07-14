import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import logoutUser from "app/views/logout/logoutUser";
import { AUTHED_USER_QUERY_KEY } from "app/hooks/useRequireAuthedUser";

export default function Logout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  React.useEffect(() => {
    logoutUser().finally(() => {
      queryClient.removeQueries({ queryKey: AUTHED_USER_QUERY_KEY });
      queryClient.removeQueries(); // clear all cached user-specific data in client.
      navigate("/login");
    });
  }, [navigate, queryClient]);

  return <LoadingIndicator />;
}
