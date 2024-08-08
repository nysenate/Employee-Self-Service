import React, { useState } from "react";
import Hero from "app/components/Hero";
import AllowanceStatusDirective from "app/views/time/allowance/AllowanceStatusDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import EmployeeSelect from "../accrual/EmployeeSelect";
import useAuth from "app/contexts/Auth/useAuth";

export default function AllowanceEmpStatusIndex() {
  const { userData } = useAuth();
  const [ selectedEmpSupInfo, setSelectedEmpSupInfo ] = useState(null);
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
      <Hero>Employee Allowed Hours</Hero>
      <EmployeeSelect
        setSelectedEmp={setSelectedEmpSupInfo}
        selectSubject={"Allowed Hours"}
        activeOnly={true}
        payType={"TE"}
      />
      {selectedEmpSupInfo && (<AllowanceStatusDirective
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