import HistoryDirective from "app/views/time/record/HistoryDirective";
import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import useAuth from "app/contexts/Auth/useAuth";
import EmployeeSelect from "../accrual/EmployeeSelect";


const RecordEmpHistoryIndex = () => {
  // Connected Components' State Variables + setter/renderer functions
  const { userData } = useAuth();
  const [selectedEmpSupInfo, setSelectedEmpSupInfo] = useState(userData().employee);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedRecord, setSelectedRecord ] = useState(null);
  const viewDetails = (selectedRecord) => {
    console.log(selectedRecord);
    setSelectedRecord(selectedRecord);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedRecord(null);
  }

  return (
    <div>
      <Hero>Employee Attendance History</Hero>
      <EmployeeSelect
        setSelectedEmp={setSelectedEmpSupInfo}
        selectSubject={"Attendance Records"}
      />
      {selectedEmpSupInfo && (<HistoryDirective
        viewDetails={viewDetails}
        user={userData().employee}
        empSupInfo={selectedEmpSupInfo}
        linkToEntryPage={true}
        scopeHideTitle={true}
      />)}
      {/*{selectedRecord && (<RecordDetailsPopup*/}
      {/*  record={selectedRecord}*/}
      {/*  isModalOpen={true}*/}
      {/*  closeModal={closeModal}*/}
      {/*/>)}*/}
    </div>
  );
}

export default RecordEmpHistoryIndex;