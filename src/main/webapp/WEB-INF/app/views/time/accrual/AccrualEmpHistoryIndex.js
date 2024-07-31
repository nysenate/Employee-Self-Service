import React, { useState } from "react";
import Hero from "app/components/Hero";
import AccrualHistoryDirective from "app/views/time/accrual/AccrualHistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import EmployeeSelect from "./EmployeeSelect";
import useAuth from "app/contexts/Auth/useAuth";

const AccrualEmpHistoryIndex = () => {
  const { userData } = useAuth();
  const [ selectedEmpSupInfo, setSelectedEmpSupInfo ] = useState(userData().employee);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);

  const viewDetails = ( record ) => {
    setSelectedAccrual(record);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAccrual(null);
  }


  return (
    <div>
      <Hero>Employee Accrual History</Hero>
      <EmployeeSelect
        setSelectedEmp={setSelectedEmpSupInfo}
        selectSubject={"Accrual History"}
      />
      {selectedEmpSupInfo && (<AccrualHistoryDirective
        viewDetails={viewDetails}
        user={userData().employee}
        empSupInfo={selectedEmpSupInfo}
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

export default AccrualEmpHistoryIndex;