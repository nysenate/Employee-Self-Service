import React, { useState } from 'react'
import Card from "app/components/Card";
import Hero from "app/components/Hero";
import LoginTitle from "app/views/login/LoginTitle";
import LoginLoadingCircle from "app/views/login/LoginLoadingCircle";
import useAuth from "app/contexts/Auth/useAuth";
import { useNavigate } from "react-router-dom";


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

function LoginForm() {
  const auth = useAuth()
  const [ errorMsg, setErrorMsg ] = useState('')
  const [ isLoading, setIsLoading ] = useState(false)

  // Form submit

  const onSubmit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    setErrorMsg('')

    try {
      auth.login(e.currentTarget.username.value, e.currentTarget.password.value)
    } finally {
      setIsLoading(false)
    }
  }

  const labelStyles = "block font-light"
  return (
    <>
      <LoginTitle>Sign in to proceed</LoginTitle>
      <form onSubmit={onSubmit}>
        <label htmlFor="username" className={labelStyles}>Username</label>
        <input id="username" type="text" name="username" autoComplete="username" className="input w-full"/>
        <label htmlFor="password" className={labelStyles + " mt-1"}>Password</label>
        <input id="password"
               type="password"
               name="password"
               autoComplete="current-password"
               className="input w-full"/>
        <div className="mt-3 flex items-center">
          <p className="w-44">
            <LoginTextLink>
              Having trouble logging in?
            </LoginTextLink>
          </p>
          {isLoading
           ? <span className="text-teal-600">
                  <LoginLoadingCircle textColor="text-teal-600"/>
                  Logging in...
                </span>
           : <button type="submit" className={`${LOGIN_BUTTON_CLASSES} grow`}>
             Login
           </button>
          }
        </div>
      </form>
      <div>
        <span className="absolute w-[296px] text-red-700"></span>
      </div>
    </>
  )
}


function LoginTextLink({ onClick, children }) {
  return (
    <span onClick={onClick}
          className="text-gray-500 transition duration-300 hover:text-teal-600 hover:cursor-pointer">
        {children}
      </span>
  )
}