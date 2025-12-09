import React from "react";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { Card } from "app/components/ui/card";

export default function SubmitApplication() {
  const { data, isPending } = useTravelApp(15);

  if (isPending) {
    return null;
  }

  return <div className="">Hello submit travel app</div>;
}
