import useAuth from "app/contexts/Auth/useAuth";
import React, { useState } from "react";
import LoginTitle from "app/views/login/LoginTitle";
import LoginLoadingCircle from "app/views/login/LoginLoadingCircle";
import { LOGIN_BUTTON_CLASSES, LOGIN_STATES } from "app/views/login/LoginIndex";
import { useForm } from "react-hook-form";


export default function LoginForm({ setState }) {
  const auth = useAuth()
  const [ isLoading, setIsLoading ] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
    clearErrors
  } = useForm({ mode: 'onSubmit' })

  const onSubmit = data => {
    setIsLoading(true)
    clearErrors()

    auth.login(data.username, data.password)
      .catch(e => setError('validationError', { type: 'custom', message: e.message }))
      .finally(() => setIsLoading(false))
  }

  const labelStyles = "block font-light"
  return (
    <>
      <LoginTitle>Sign in to proceed</LoginTitle>
      <form onSubmit={handleSubmit(onSubmit)}>
        <label htmlFor="username" className={labelStyles}>Username</label>
        <input
          {...register("username", { required: "Please enter your username." })}
          type="text"
          autoComplete="username"
          className={`input w-full ${errors.username ? 'input--invalid' : ''}`}
          aria-invalid={errors.username ? "true" : "false"}
        />
        <label htmlFor="password" className={labelStyles + " mt-1"}>Password</label>
        <input
          {...register("password", { required: "Please enter your password." })}
          type="password"
          autoComplete="current-password"
          className={`input w-full ${errors.password ? 'input--invalid' : ''}`}
          aria-invalid={errors.password ? "true" : "false"}
        />
        <div className="mt-3 flex items-center">
          <p className="w-44">
            <LoginTextLink onClick={() => setState(LOGIN_STATES.HELP)}>
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
        <span className="absolute w-[296px] text-red-700">
          {errorMessage(errors)}
        </span>
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

/**
 * Controls which error message is displayed, displaying only one at a time.
 */
function errorMessage(errors) {
  if (errors.username) {
    return errors.username.message
  }
  if (errors.password) {
    return errors.password.message
  }
  if (errors.validationError) {
    return errors.validationError.message
  }
  return ''
}