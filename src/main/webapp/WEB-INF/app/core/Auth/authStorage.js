import { formatISO, parseISO } from "date-fns";

const IS_AUTHED_KEY = "ess.auth.isAuthed"
const EXPIRES_TIME = "ess.auth.expiresTime"
const EMP_ID = "ess.auth.empId"

// const USER_ROLES = "ess.user.roles"

export function saveAuth(isAuthed = false,
                         expiresTime = new Date(),
                         empId = null) {
  localStorage.setItem(IS_AUTHED_KEY, JSON.stringify(isAuthed))
  localStorage.setItem(EXPIRES_TIME, formatISO(expiresTime))
  localStorage.setItem(EMP_ID, JSON.stringify(empId))
}

export function loadAuth() {
  const isAuthed = JSON.parse(localStorage.getItem(IS_AUTHED_KEY)) || false
  const expiresTime = parseISO(localStorage.getItem(EXPIRES_TIME) || formatISO(new Date()))
  const empId = JSON.parse(localStorage.getItem(EMP_ID)) || null
  return {
    isAuthed,
    expiresTime,
    empId
  }
}