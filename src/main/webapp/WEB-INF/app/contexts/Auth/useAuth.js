import React, { useEffect, useState } from "react";
import { loadAuth, saveAuth } from "app/contexts/Auth/authStorage";
import { add, isAfter } from "date-fns";
import { fetchApiJson, fetchJson } from "app/api/fetchJson";

const AuthContext = React.createContext();

function useProvideAuth() {
  const localStorageAuth = loadAuth();
  const [isAuthed, setIsAuthed] = useState(localStorageAuth.isAuthed);
  const [isAuthLoading, setIsAuthLoading] = useState(true);
  const [expiresTime, setExpiresTime] = useState(localStorageAuth.expiresTime);
  const [empId, setEmpId] = useState(localStorageAuth.empId);

  const isExpired = () => {
    return isAfter(new Date(), expiresTime);
  };

  /**
   * On mount check the backend for a valid session and synchronize it with front end.
   */
  useEffect(() => {
    const checkServerAuthStatus = async () => {
      fetchApiJson("/employees/me")
        .then((res) => {
          setIsAuthed(true);
          setEmpId(res.result.employeeId);
          setExpiresTime(add(new Date(), { minutes: 10 }));
        })
        .catch((error) => setIsAuthed(false))
        .finally(() => setIsAuthLoading(false));
    };

    if (!isAuthed || isExpired()) {
      checkServerAuthStatus();
    } else {
      setIsAuthLoading(false);
    }
  }, []);

  useEffect(() => {
    saveAuth(isAuthed, expiresTime, empId);
  }, [isAuthed, expiresTime, empId]);

  return {
    isAuthed() {
      return isAuthed && !isExpired();
    },
    isAuthLoading() {
      return isAuthLoading;
    },
    empId() {
      return empId;
    },
    login(username, password) {
      return loginUser(username, password).then((data) => {
        setIsAuthed(data.authenticated);
        setEmpId(data.employeeId);
        setExpiresTime(add(new Date(), { minutes: 10 }));
      });
    },
    logout() {
      return logoutUser().then(() => {
        setIsAuthed(false);
        setEmpId(null);
      });
    },
  };
}

export function AuthProvider({ children }) {
  const auth = useProvideAuth();

  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
}

export default function useAuth() {
  return React.useContext(AuthContext);
}

async function loginUser(username, password) {
  const body = new URLSearchParams();
  body.append("username", username);
  body.append("password", password);

  let data;
  try {
    data = await fetchJson(`/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: body,
    });
  } catch (error) {
    console.error(error);
  }

  if (data.authenticated) {
    // successfully logged in.
    return data;
  } else {
    // Unsuccessful login.
    throw new Error(data.message);
  }
}

async function logoutUser() {
  const res = await fetch("/logout", { method: "GET" });
}
