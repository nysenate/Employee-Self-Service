import React, { useCallback, useEffect, useState } from 'react'
import { loadAuth, saveAuth } from "app/contexts/Auth/authStorage";
import { add, isAfter } from "date-fns";
import { fetchApiJson, fetchJson } from "app/utils/fetchJson";


const AuthContext = React.createContext()

function useProvideAuth() {
  const localStorageAuth = loadAuth()
  const [ isAuthed, setIsAuthed ] = useState(localStorageAuth.isAuthed)
  const [ expiresTime, setExpiresTime ] = useState(localStorageAuth.expiresTime)
  const [ empId, setEmpId ] = useState(localStorageAuth.empId)
  const [userData, setUserData] = useState(localStorageAuth.userData);

  const isExpired = () => {
    return isAfter(new Date(), expiresTime)
  }

  React.useEffect(() => {
    // TODO will this overwrite local storage on initial load?
    // TODO save isAuthed = isAuthed()???? so once expires isAuthed = false
    saveAuth(isAuthed, expiresTime, empId, userData)
  }, [ isAuthed, expiresTime, empId, userData ])

  const fetchUserData = useCallback(async () => {
    if (empId) {
      const userResponse = await fetchApiJson(`/employees?detail=true&empId=${empId}`)
      setUserData(userResponse)
    }
  }, [empId])

  useEffect(() => {
    if (isAuthed && !userData) {
      fetchUserData();
    }
  }, [isAuthed, fetchUserData, userData]);

  return {
    isAuthed() {
      return isAuthed && !isExpired()
    },
    empId() {
      return empId
    },
    userData() {
      return userData
    },
    login(username, password) {
      return loginUser(username, password)
        .then((data) => {
          setIsAuthed(data.authenticated)
          setExpiresTime(add(new Date(), { minutes: 10 }))
          setEmpId(data.employeeId)
          fetchUserData() // Fetch user data after login
        })
    },
    logout() {
      return logoutUser()
        .then(() => {
          setIsAuthed(false)
          setEmpId(null)
          setUserData(null) // Clear user data on logout
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
          "Content-Type": "application/x-www-form-urlencoded",
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