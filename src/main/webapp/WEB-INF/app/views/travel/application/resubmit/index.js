import React from "react";
import { useParams } from "react-router-dom";
import { parseApplicationId } from "app/views/travel/application/workflow/applicationRoutes";
import ResubmitTravelApplication from "./ResubmitTravelApplication";

export default function ResubmitTravelApplicationPage() {
  const { appId: routeAppId } = useParams();
  const appId = parseApplicationId(routeAppId);

  return <ResubmitTravelApplication key={String(appId)} appId={appId} />;
}
