import React from "react"
import { useNavigate } from "react-router-dom";
import LoadingIndicator from "app/components/LoadingIndicator";


export default function EssIndex() {
  const navigate = useNavigate()

  React.useEffect(() => {
    navigate("/login")
  }, [])

  return <LoadingIndicator/>
}