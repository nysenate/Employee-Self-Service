import React, { useState } from "react";
import Hero from "app/components/Hero";
import HistoryDirective from "app/views/time/accrual/HistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";

const AccrualHistoryIndex = () => {
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
      <HistoryDirective
        viewDetails={viewDetails}
        empSupInfo={null}
      />
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