import React, { useState } from 'react'
import Card from "app/components/Card";
import Hero from "app/components/Hero";
import useAuth from "app/contexts/Auth/useAuth";
import { useNavigate } from "react-router-dom";
import LoginForm from "app/views/login/LoginForm";
import LoginDevBanner from "app/views/login/LoginDevBanner";


export const LOGIN_BUTTON_CLASSES = `py-0.5 bg-gray-100 border-1 border-gray-400 transition
duration-500 hover:bg-gray-50 hover:text-teal-600 disabled:pointer-events-none disabled:opacity-50`

export default function LoginIndex() {
  const navigate = useNavigate();
  const auth = useAuth()

  React.useEffect(() => {
    // If the user is already logged in, redirect them.
    if (auth.isAuthed()) {
      navigate("/myinfo")
    }
  }, [ auth ])

  return (
    <div>
      {process.env.RUNTIME_LEVEL === 'dev' && <LoginDevBanner/>}
      <div className="relative h-screen flex justify-center items-center">
        <Card className="border-b-4 border-teal-600">
          <Hero>New York State Senate Employee Self Service</Hero>
          <div className="flex">
            <img src="/assets/img/capital-exterior.jpg"
                 width="400"
                 height="270"
                 alt="New York State Senate Capital Building"/>
            <div className="w-[400px] pl-10 py-10 pr-16 bg-white">
              <LoginForm/>
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}
