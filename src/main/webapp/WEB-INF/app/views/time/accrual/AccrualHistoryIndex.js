import React, { useState } from "react";
import Hero from "app/components/Hero";
import AccrualHistoryDirective from "app/views/time/accrual/AccrualHistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import useAuth from "app/contexts/Auth/useAuth";

const AccrualHistoryIndex = () => {
  const { userData } = useAuth();
  // Connected Components' State Variables + setter/renderer functions
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);
  const viewDetails = (accrualRecord) => {
    console.log(accrualRecord);
    setSelectedAccrual(accrualRecord);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAccrual(null);
  }

  return (
    <div>
      <Hero>Accrual History</Hero>
      {userData().employee && (<AccrualHistoryDirective
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
  );
}

export default AccrualHistoryIndex;