import React from "react"
import { useNavigate } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";


export default function PrivateContent({ children }) {
  const auth = useAuth()
  const navigate = useNavigate()

  React.useEffect(() => {
    if (!auth.isAuthed()) {
      navigate("/login")
    }
  }, [])

  if (auth.isAuthed()) {
    return { children }
  } else {
    return null
  }
};