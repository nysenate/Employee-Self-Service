import React from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import AlertInfoForm from "app/views/myinfo/personnel/emergency-alert-info/AlertInfoForm";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useEmployeeAlertInfo } from "app/views/myinfo/personnel/emergency-alert-info/useEmployeeAlertInfo";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function EmergencyAlertInfoIndex() {
  const { data: user } = useRequireAuthedUser();
  const alertInfo = useEmployeeAlertInfo(user?.employeeId);

  return (
    <div>
      <Hero>Emergency Alert Info</Hero>
      <Card className="mt-5">
        <Card.Header>
          The following contact information will be used to reach you in the
          event of a Senate-wide emergency.
        </Card.Header>

        {alertInfo.isPending && <LoadingIndicator />}
        {alertInfo.isSuccess && <AlertInfoForm alertInfo={alertInfo.data} />}
      </Card>
    </div>
  );
}
