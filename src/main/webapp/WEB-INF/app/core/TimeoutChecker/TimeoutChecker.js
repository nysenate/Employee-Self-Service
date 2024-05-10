import React from "react"
import { pingApi } from "app/core/TimeoutChecker/pingApi";
import Modal from "app/components/Modal";


// Values are in Seconds.
const PING_RATE = 5
const WARNING_THRESHOLD = 70
const PING_FAIL_TOLERANCE = 10

export default function TimeoutChecker({ children }) {
  // The length of time remaining before the user is logged out if they do not perform an action.
  const [ remainingTime, setRemainingTime ] = React.useState()
  const [ isActive, setIsActive ] = React.useState(true)
  const [ failedPings, setFailedPings ] = React.useState(0)

  const ping = (isActive) => {
    pingApi(isActive)
      .then((res) => {
        setFailedPings(0)
        setRemainingTime(res.remainingInactivity)
        setIsActive(false) // reset active flag.
      })
      .catch((err) => {
        console.log(err)
        setFailedPings((failedPings) => failedPings + 1)
      })
  }

  React.useEffect(() => {
    if (remainingTime < 0 || failedPings >= PING_FAIL_TOLERANCE) {
      window.location.replace(`${process.env.DOMAIN_URL}/logout`)
      console.log("Logout user")
    } else if (remainingTime <= WARNING_THRESHOLD) { // TODO and modal not already open
      console.log("show idle prompt modal")
    }
  }, [ remainingTime, failedPings ])

  const setActive = () => {
    setIsActive(true)
  }

  React.useEffect(() => {
    document.addEventListener("change", setActive)
    document.addEventListener("keydown", setActive)
    document.addEventListener("click", setActive)
    document.addEventListener("keydown", setActive)
    document.addEventListener("scroll", setActive)
    return () => {
      document.removeEventListener("change", setActive)
      document.removeEventListener("keydown", setActive)
      document.removeEventListener("click", setActive)
      document.removeEventListener("keydown", setActive)
      document.removeEventListener("scroll", setActive)
    }
  }, [])

  React.useEffect(() => {
    const interval = setInterval(() => ping(isActive), PING_RATE * 1000)
    return () => clearInterval(interval)
  }, [ isActive ])

  return (
    <div>
      {children}
    </div>
  )
}
