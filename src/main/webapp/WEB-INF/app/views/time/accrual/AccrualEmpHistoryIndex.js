import React, { useState } from "react";
import styles from "../universalStyles.module.css"
import Hero from "app/components/Hero";
import HistoryDirective from "app/views/time/accrual/HistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import EmpolyeeSelect from "./EmployeeSelect";

const AccrualEmpHistoryIndex = () => {
  const [ selectedEmp, setSelectedEmp ] = useState(null);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);

  const viewDetails = ({ record }) => {
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
      <EmpolyeeSelect selectedEmp={selectedEmp}/>
      <HistoryDirective
        viewDetails={viewDetails}
        empSupInfo={selectedEmp}
      />
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