import React from "react"
import LoginTitle from "app/views/login/LoginTitle";
import { LOGIN_BUTTON_CLASSES, LOGIN_STATES } from "app/views/login/LoginIndex";

export default function LoginHelp({ setState }) {
  return (
    <>
      <LoginTitle>Phone Support</LoginTitle>
      <p>
        For technical problems call:<br/>
        <span className="font-semibold">STS Help Line - x{process.env.HELPLINE_PHONE_NUMBER_EXTENSION}</span>
      </p>
      <p className="my-3">
        For Personnel related questions:<br/>
        <span
          className="font-semibold">Senate Personnel Office - {process.env.PERSONNEL_PHONE_NUMBER}</span>
      </p>
      <button type="button"
              className={`${LOGIN_BUTTON_CLASSES} w-1/2`}
              onClick={() => setState(LOGIN_STATES.LOGIN)}>
        Return to login
      </button>
    </>
  )
}
