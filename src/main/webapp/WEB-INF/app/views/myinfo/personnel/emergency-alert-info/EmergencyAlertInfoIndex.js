import React from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { loadAuth } from "app/contexts/Auth/authStorage";
import { fetchApiJson } from "app/utils/fetchJson";
import { json, useLoaderData } from "react-router-dom";
import AlertInfoForm from "app/views/myinfo/personnel/emergency-alert-info/AlertInfoForm";


export async function emergencyAlertLoader() {
  const auth = loadAuth()
  const alertInfo = await fetchApiJson(`/alert-info?empId=${auth.empId}`)
    .then((body) => body.result)
  return json({ alertInfo: alertInfo })
}

export default function EmergencyAlertIndex() {
  const { alertInfo } = useLoaderData()
  return (
    <div>
      <Hero>Emergency Alert Info</Hero>
      <Card className="mt-5">
        <Card.Header>
          The following contact information will be used to reach you in the
          event of a Senate-wide emergency.
        </Card.Header>

        <AlertInfoForm alertInfo={alertInfo}/>
      </Card>
    </div>
  )
}
