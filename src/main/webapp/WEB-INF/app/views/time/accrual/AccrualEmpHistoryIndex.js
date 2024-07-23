import React from "react";
import styles from "../universalStyles.module.css"
import Hero from "app/components/Hero";
import HistoryDirective from "app/views/time/accrual/HistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import AccrualHistoryIndex from "app/views/time/accrual/AccrualHistoryIndex";

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
      <select></select>
      {selectedEmp.empId && <HistoryDirective
        empSupInfo={selectedEmp}
        activeYears={activeYears}
        accSummaries={accSummaries}
        viewDetails={viewDetails}
      />}
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