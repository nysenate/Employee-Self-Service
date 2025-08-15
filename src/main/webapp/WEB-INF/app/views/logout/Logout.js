import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import logoutUser from "app/views/logout/logoutUser";

export default function Logout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  React.useEffect(() => {
    logoutUser()
      .then(() => queryClient.removeQueries()) // clear all cached data in client.
      .then(() => fetch("/logout")) // logout on the backend.
      .then(() => navigate("/login"));
  }, []);

  return <LoadingIndicator />;
}
