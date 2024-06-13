import React from "react"
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";


export default function Logout() {
  const auth = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  React.useEffect(() => {
    auth.logout()
      .then(() => queryClient.removeQueries()) // clear all cached data on logout
      .then(() => navigate("/login"))
  }, [])

  return (
    <LoadingIndicator/>
  )
}