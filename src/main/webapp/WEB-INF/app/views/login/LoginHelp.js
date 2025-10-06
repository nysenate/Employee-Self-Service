import React from "react";
import LoginTitle from "app/views/login/LoginTitle";
import { LOGIN_BUTTON_CLASSES, LOGIN_STATES } from "app/views/login/LoginIndex";
import { useConfig } from "app/core/useConfig";

export default function LoginHelp({ setState }) {
  const { data: config } = useConfig();
  return (
    <>
      <LoginTitle>Phone Support</LoginTitle>
      <p>
        For technical problems call:
        <br />
        <span className="font-semibold">
          STS Help Line - {config?.helplinePhoneNumber}
        </span>
      </p>
      <p className="my-3">
        For Personnel related questions:
        <br />
        <span className="font-semibold">
          Senate Personnel Office - {config?.personnelPhoneNumber}
        </span>
      </p>
      <button
        type="button"
        className={`${LOGIN_BUTTON_CLASSES} w-1/2`}
        onClick={() => setState(LOGIN_STATES.LOGIN)}
      >
        Return to login
      </button>
    </>
  );
}
