import React, { useState } from "react";
import Hero from "app/components/Hero";
import AllowanceStatusDirective from "app/views/time/allowance/AllowanceStatusDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import useAuth from "app/contexts/Auth/useAuth";

export default function AllowanceStatusIndex() {
  const { userData } = useAuth();
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);

  const viewDetails = (accrual) => {
    setSelectedAccrual(accrual);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAccrual(null);
  }


  return (
    <div>
      <Hero>Allowed Hours</Hero>
      {userData().employee && (<AllowanceStatusDirective
        viewDetails={viewDetails}
        user={userData().employee}
        empSupInfo={userData().employee}
      />)}
      {selectedAccrual && (
        <AccrualDetailsPopup
          accruals={selectedAccrual}
          isModalOpen={isModalOpen}
          closeModal={closeModal}
        />
      )}
    </div>
  )
}