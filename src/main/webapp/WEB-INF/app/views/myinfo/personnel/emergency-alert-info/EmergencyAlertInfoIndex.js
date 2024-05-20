import React from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { loadAuth } from "app/core/Auth/authStorage";

export async function emergencyAlertLoader() {
  const auth = loadAuth()

}

export default function EmergencyAlertIndex() {
  return (
    <div>
      <Hero>Emergency Alert Info</Hero>
      <Card className="mt-5">
        <Card.Header>
          The following contact information will be used to reach you in the
          event of a Senate-wide emergency.
        </Card.Header>

        {/*<AlertInfoForm alertInfo={alertInfo}/>*/}
      </Card>
    </div>
  )
}
