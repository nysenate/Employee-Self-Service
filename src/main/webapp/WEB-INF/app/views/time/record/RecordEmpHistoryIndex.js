import HistoryDirective from "app/views/time/record/HistoryDirective";
import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import useAuth from "app/contexts/Auth/useAuth";
import { fetchEmployeeInformation } from "app/views/supply/helpers";
import EmployeeSelect from "../accrual/EmployeeSelect";


const RecordEmpHistoryIndex = () => {
  // Connected Components' State Variables + setter/renderer functions
  const auth = useAuth();
  const [user, setUser] = useState({});
  const [selectedEmpSupInfo, setSelectedEmpSupInfo] = useState({});
  const [selectedSup, setSelectedSup] = useState({});
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

  useEffect(() => {
    const getUserInfo = async () => {
      try {
        const response = await fetchEmployeeInformation(auth.empId());
        console.log(response.employee);
        setUser(response.employee);
        if(!selectedEmpSupInfo) setSelectedEmpSupInfo(response.employee);
      }catch (err){
        console.error("Error fetchEmployeeInformation(", auth.empId(), "): ", err);
      }
    }
    getUserInfo();
  }, [auth]);

  return (
    <div>
      <Hero>Employee Attendance History</Hero>
      <EmployeeSelect
        selectedSup={selectedSup}
        setSelectedSup={setSelectedSup}
        selectedEmp={selectedEmpSupInfo}
        setSelectedEmp={setSelectedEmpSupInfo}
        selectSubject={"Attendance Records"}
      />
      {selectedEmpSupInfo && (<HistoryDirective
        viewDetails={viewDetails}
        user={user}
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