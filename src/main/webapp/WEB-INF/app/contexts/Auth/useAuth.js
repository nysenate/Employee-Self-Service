import React, { useState } from 'react'
import { loadAuth, saveAuth } from "app/contexts/Auth/authStorage";
import { add, isAfter } from "date-fns";
import { fetchJson } from "app/utils/fetchJson";


const AuthContext = React.createContext()

function useProvideAuth() {
  const localStorageAuth = loadAuth()
  const [ isAuthed, setIsAuthed ] = useState(localStorageAuth.isAuthed)
  const [ expiresTime, setExpiresTime ] = useState(localStorageAuth.expiresTime)
  const [ empId, setEmpId ] = useState(localStorageAuth.empId)

  const isExpired = () => {
    return isAfter(new Date(), expiresTime)
  }

  React.useEffect(() => {
    console.log(isAuthed)
    console.log(expiresTime)
    console.log(empId)
    // TODO will this overwrite local storage on initial load?
    // TODO save isAuthed = isAuthed()???? so once expires isAuthed = false
    saveAuth(isAuthed, expiresTime, empId)
  }, [ isAuthed, expiresTime, empId ])

  return {
    isAuthed() {
      return isAuthed && !isExpired()
    },
    empId() {
      return empId
    },
    login(username, password) {
      return loginUser(username, password)
        .then((data) => {
          setIsAuthed(data.authenticated)
          setExpiresTime(add(new Date(), { minutes: 10 }))
          setEmpId(data.employeeId)
        })
    },
    logout() {
      return logoutUser()
        .then(() => {
          setIsAuthed(false)
          setEmpId(null)
        })
    }
  }
}

export function AuthProvider({ children }) {
  const auth = useProvideAuth()

  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  )
}

export default function useAuth() {
  return React.useContext(AuthContext)
}

async function loginUser(username, password) {
  const body = new URLSearchParams()
  body.append("username", username)
  body.append("password", password)

  let data
  try {
    data = await fetchJson(`/login`, {
        method: 'POST',
        headers: {
          "Content-Type": "application/json",
        },
        body: body,
      }
    )
  } catch (error) {
    console.error(error)
  }

  if (data.authenticated) {
    // successfully logged in.
    return data;
  } else {
    // Unsuccessful login.
    throw new Error(data.message)
  }
}

async function logoutUser() {
  const res = await fetch('/logout', { method: 'GET' })
}