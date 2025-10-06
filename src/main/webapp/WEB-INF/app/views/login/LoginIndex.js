import React, { useEffect, useState } from "react";
import Card from "app/components/Card";
import Hero from "app/components/Hero";
import { useNavigate } from "react-router-dom";
import LoginForm from "app/views/login/LoginForm";
import LoginDevBanner from "app/views/login/LoginDevBanner";
import LoginHelp from "app/views/login/LoginHelp";
import useAuthedUser, {
  useAuthedUserNoRedirect,
} from "app/hooks/useAuthedUser";
import { useConfig } from "app/hooks/useConfig";

export const LOGIN_STATES = {
  LOGIN: 0,
  HELP: 1,
};

export const LOGIN_BUTTON_CLASSES = `py-0.5 bg-gray-100 border-1 border-gray-400 transition
duration-500 hover:bg-gray-50 hover:text-teal-600 disabled:pointer-events-none disabled:opacity-50`;

export default function LoginIndex() {
  const { data: config } = useConfig();
  const { data: user, isPending } = useAuthedUserNoRedirect();
  const navigate = useNavigate();
  const [state, setState] = useState(LOGIN_STATES.LOGIN);

  useEffect(() => {
    // If the user is already logged in, redirect them.
    if (user) {
      navigate("/myinfo");
    }
  }, [user]);

  if (isPending) {
    return <></>;
  }

  return (
    <div>
      {config?.runtimeLevel === "dev" && <LoginDevBanner />}
      <div className="relative flex h-screen items-center justify-center">
        <Card className="border-b-4 border-teal-600">
          <Hero>New York State Senate Employee Self Service</Hero>
          <div className="flex">
            <img
              src="/assets/img/capital-exterior.jpg"
              width="400"
              height="270"
              alt="New York State Senate Capital Building"
            />
            <div className="w-[400px] bg-white py-10 pl-10 pr-16">
              {state === LOGIN_STATES.LOGIN && (
                <LoginForm setState={setState} />
              )}
              {state === LOGIN_STATES.HELP && <LoginHelp setState={setState} />}
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
