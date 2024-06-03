import React from "react"
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useNavigate } from "react-router-dom";


export default function Logout() {
  const auth = useAuth()
  const navigate = useNavigate()

  React.useEffect(() => {
    auth.logout()
      .then(() => navigate("/login"))
  }, [])

  return (
    <LoadingIndicator/>
  )
}