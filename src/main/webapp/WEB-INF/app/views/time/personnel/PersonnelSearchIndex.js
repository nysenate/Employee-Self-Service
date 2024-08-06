import React, { useEffect, useState } from 'react';
import Hero from "app/components/Hero";
import EmployeeSearchDirective from "app/views/time/personnel/EmployeeSearchDirective";
import EssNotification from "app/components/EssNotification";
import { RecordDetailsPopup } from "app/views/time/record/RecordDetailsPopup";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import TogglePanel from "app/components/TogglePanel";
import RecordHistoryDirective from "app/views/time/record/RecordHistoryDirective";
import useAuth from "app/contexts/Auth/useAuth";
import AccrualBar from "app/views/time/accrual/AccrualBar";
import AllowanceBar from "app/views/time/allowance/AllowanceBar";
import AllowanceHistoryDirective from "app/views/time/allowance/AllowanceHistoryDirective";
import AccrualProjectionsDirective from "app/views/time/accrual/AccrualProjectionsDirective";
import AccrualHistoryDirective from "app/views/time/accrual/AccrualHistoryDirective";
import { fetchAccrualActiveYears } from "app/views/time/accrual/time-accrual-ctrl";
import { fetchAllowancesActiveYears } from "app/views/time/allowance/time-allowance-ctrl";
import { fetchEmployeeSearchApi, getSearchParam } from "app/views/time/personnel/personnel-Api-ctrl";

// Abisha Vijayashanthar 14160 => AccBar, AttendHist, AccHist, AccProj
// Maya L. Allen 14421 => AllowBar, (AttHist), AllowHist
export default function PersonnelSearchIndex() {
  const { userData } = useAuth();
  const [empId, setEmpId] = useState(parseInt(getSearchParam('empId') || NaN))
  const [selectedEmp, setSelectedEmp] = useState(null);

  const [showAllowanceHistory, setShowAllowanceHistory] = useState(false);
  const [showAccrualHistory, setShowAccrualHistory] = useState(false);
  const [showAccruals, setShowAccruals] = useState(false);

  useEffect(() => {
    if(empId) getSelectedEmp();
  }, [empId]);

  useEffect(() => {
    if(selectedEmp && empId) {
      getAccrualYears();
      getAllowanceYears();
    }
  }, [selectedEmp]);

  // Fetch+Compute Component Display Logic Variables+Flags
  async function getSelectedEmp() {
    try {
      const params = {
        empId: empId,
      }
      const response = await fetchEmployeeSearchApi(params);
      setSelectedEmp(response.employees[0]);
    } catch (err) {
      console.error(err);
    }
  }
  const getAccrualYears = async() => {
    setShowAccrualHistory(false);
    setShowAccruals(false);
    if(!selectedEmp) return;
    const currentYear = new Date().getFullYear();

    const params = {empId: selectedEmp.empId};
    try {
      const response = await fetchAccrualActiveYears(params);
      let showAcrlHist = response.years && response.years.length > 0;
      let showAcrls = showAcrlHist && response.years.includes(currentYear)&&
        selectedEmp.payType !== 'TE' && !selectedEmp.senator;
      setShowAccrualHistory(showAcrlHist);
      setShowAccruals(showAcrls);
    } catch(err) {
      console.error(err);
    }
  }
  const getAllowanceYears = async() => {
    setShowAccrualHistory(false);
    if(!selectedEmp) return;

    const params = {empId: selectedEmp.empId};
    try {
      const response = await fetchAllowancesActiveYears(params);
      setShowAllowanceHistory(response.years && response.years.length > 0)
    } catch(err) {
      console.error(err);
    }
  }

  // Popups:
  const [isRecordModalOpen, setIsRecordModalOpen] = useState(false);
  const [ selectedRecord, setSelectedRecord ] = useState(null);
  const [isAccrualModalOpen, setIsAccrualModalOpen] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);
  const closeModal = () => {setIsRecordModalOpen(false);setIsAccrualModalOpen(false);}
  const viewRecordDetails = (record) => {
    console.log("viewRecordDetails input: ", record);
    setSelectedRecord(record);
    setIsRecordModalOpen(true);
  }
  const viewAccrualDetails = (accrual) => {
    console.log("viewAccrualDetails input: ", accrual);
    setSelectedAccrual(accrual);
    setIsAccrualModalOpen(true);
  }

  return (
    <div>
      <Hero>Employee Search</Hero>
      <EmployeeSearchDirective selectedEmp={selectedEmp} setSelectedEmp={setSelectedEmp}/>
      {selectedEmp && (
        <div>
          {!(selectedEmp.active) && (
            <EssNotification level="info" title={`${selectedEmp.fullName} is not a current Senate employee.`}/>
          )}
          {selectedEmp.senator && (
            <EssNotification level="info" title={`${selectedEmp.fullName} is a Senator`}>
              <p>
                They cannot use or project accruals.
                <br />
                They will not have any attendance or accrual history unless they were a non-senator employee in the past.
              </p>
            </EssNotification>
          )}
          {selectedEmp.active && (
            <div>
              {showAccruals && <AccrualBar empId={empId}/>}
              {selectedEmp.payType === 'TE' && <AllowanceBar empId={empId}/>}
            </div>
          )}

          <TogglePanel open={false} label={"Attendance History"} >
            <RecordHistoryDirective
              viewDetails={viewRecordDetails}
              user={userData().employee}
              empSupInfo={selectedEmp}
              linkToEntryPage={true}
              scopeHideTitle={true}
            />
          </TogglePanel>
          {showAccrualHistory && (<TogglePanel open={false} label={"Accrual History"}>
            <AccrualHistoryDirective
              viewDetails={viewAccrualDetails}
              user={userData().employee}
              empSupInfo={selectedEmp}
              scopeHideTitle={true}
            />
          </TogglePanel>)}
          {selectedEmp?.payType !== 'TE' && !selectedEmp?.senator && (<TogglePanel open={false} label={"Accrual Projections"}>
            <AccrualProjectionsDirective
              viewDetails={viewAccrualDetails}
              user={userData().employee}
              empSupInfo={selectedEmp}
              scopeHideTitle={true}
            />
          </TogglePanel>)}
          {showAllowanceHistory && (<TogglePanel open={false} label={"Allowance History"}>
            <AllowanceHistoryDirective
              user={userData().employee}
              empSupInfo={selectedEmp}
              scopeHideTitle={true}
            />
          </TogglePanel>)}

        </div>
      )}
      {selectedRecord && (
        <RecordDetailsPopup
          record={selectedRecord}
          isModalOpen={isRecordModalOpen}
          closeModal={closeModal}
        />
      )}
      {selectedAccrual && (
        <AccrualDetailsPopup
          accruals={selectedAccrual}
          isModalOpen={isAccrualModalOpen}
          closeModal={closeModal}
        />
      )}
    </div>
  );
};
