import { Button } from "app/components/Button";
import React, { useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import useAuth from "app/contexts/Auth/useAuth";
import { useSupplyDestinations } from "app/views/supply/requisition/useSupplyDestinations";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useEmployee } from "app/views/useEmployee";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";

export default function SelectDestination() {
  const auth = useAuth()
  const employeeQuery = useEmployee(auth.empId())
  const validDestinationsQuery = useSupplyDestinations(auth.empId())
  const { setDestination } = useSupplyContext()
  const [dirtyDestination, setDirtyDestination] = useState()

  // Attempt to initialize the dirtyDestination to the employees work location.
  React.useEffect(() => {
    if (validDestinationsQuery.data && employeeQuery.data) {
      const workLocationId = employeeQuery.data.empWorkLocation.locId
      let initDestination = validDestinationsQuery.data.find(d => d.locId === workLocationId)
      if (!initDestination) {
        initDestination = validDestinationsQuery.data[0]
      }
      setDirtyDestination(initDestination);
    }
  }, [validDestinationsQuery.data, employeeQuery.data])

  const onDirtyDestinationChange = (e) => {
    const selectedId = e.target.value;
    const selectedLocation = validDestinationsQuery.data.find(
      location => location.locId === selectedId
    );
    setDirtyDestination(selectedLocation);
  };

  if (validDestinationsQuery.isPending || employeeQuery.isPending) {
    return <LoadingIndicator/>
  }

  return (
    <div>
      <Hero>Requisition Form</Hero>
      <Controls className="text-center p-3">
        <label className="text-purple-700 font-semibold mr-3" htmlFor="destination">Please select a destination:</label>
        <select
          id="destination"
          name="destination"
          className="select mr-3"
          value={dirtyDestination?.locId || ''}
          onChange={onDirtyDestinationChange}
        >
          <option value="">Select a destination</option>
          {validDestinationsQuery.data.map((location) => (
            <option key={location.locId} value={location.locId}>
              {location.code} ({location.locationDescription})
            </option>
          ))}
        </select>
        <Button
          onClick={() => setDestination(dirtyDestination)}
          disabled={!dirtyDestination}
        >
          Confirm
        </Button>
      </Controls>
    </div>
  );
}