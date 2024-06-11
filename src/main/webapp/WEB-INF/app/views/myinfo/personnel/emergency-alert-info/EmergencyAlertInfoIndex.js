import React from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import AlertInfoForm from "app/views/myinfo/personnel/emergency-alert-info/AlertInfoForm";
import { useAlertInfo } from "app/api/alertInfoApi";
import LoadingIndicator from "app/components/LoadingIndicator";


export default function EmergencyAlertInfoIndex() {
  const alertInfo = useAlertInfo()

  return (
    <div>
      <Hero>Emergency Alert Info</Hero>
      <Card className="mt-5">
        <Card.Header>
          The following contact information will be used to reach you in the
          event of a Senate-wide emergency.
        </Card.Header>

        {alertInfo.isPending && <LoadingIndicator/>}
        {alertInfo.isSuccess && <AlertInfoForm alertInfo={alertInfo.data}/>}
      </Card>
    </div>
  )
}
