import React, { useState } from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { fetchApiJson } from "app/utils/fetchJson";
import AlertInfoForm from "app/views/myinfo/personnel/emergency-alert-info/AlertInfoForm";
import useAuth from "app/contexts/Auth/useAuth";


const getAlertInfo = async empId => {
  return await fetchApiJson(`/alert-info?empId=${empId}`)
    .then((body) => body.result)
}

export default function EmergencyAlertInfoIndex() {
  const auth = useAuth()
  const [ alertInfo, setAlertInfo ] = useState()

  React.useEffect(() => {
    getAlertInfo(auth.empId())
      .then((info) => setAlertInfo(info))
  }, [])

  return (
    <div>
      <Hero>Emergency Alert Info</Hero>
      <Card className="mt-5">
        <Card.Header>
          The following contact information will be used to reach you in the
          event of a Senate-wide emergency.
        </Card.Header>

        {alertInfo && <AlertInfoForm alertInfo={alertInfo}/>}
      </Card>
    </div>
  )
}
