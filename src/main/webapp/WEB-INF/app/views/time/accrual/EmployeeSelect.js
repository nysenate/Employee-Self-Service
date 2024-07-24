import React, { useState, useEffect } from 'react';
import styles from "app/views/time/universalStyles.module.css";
import { useSupEmpGroupService } from '../accrual/supEmpGroupService';

const EmployeeSelect = ({ selectedSup, selectedEmp, setSelectedSup, setSelectedEmp, activeOnly = false, showSenators = false, payType, selectSubject = 'info' }) => {
  const [iSelEmpGroup, setISelEmpGroup] = useState(-1);
  const [iSelEmp, setISelEmp] = useState(-1);
  const { loading, getEmpInfos, getSupEmpGroupList } = useSupEmpGroupService();
  const supEmpGroups = getSupEmpGroupList();
  const [allEmps, setAllEmps] = useState([]);
  const validSupEmpGroupCount = supEmpGroups.length;

  useEffect(() => {
    if (iSelEmpGroup >= 0) {
      const emps = getEmpInfos(iSelEmpGroup, !showSenators);
      console.log("emps", emps);
      // const filteredEmps = emps.filter(emp => employeeFilter(emp));
      setAllEmps(emps);
      setSelectedSup(supEmpGroups[iSelEmpGroup]);
      if (iSelEmp === 0) {
        setSelectedEmp(filteredEmps[0]);
      } else {
        setISelEmp(0);
      }
    }
  }, [iSelEmpGroup]);

  useEffect(() => {
    if (iSelEmp >= 0) {
      setSelectedEmp(allEmps[iSelEmp]);
    }
  }, [iSelEmp]);

  useEffect(() => {
    console.log("allEmps:", allEmps);
  }, [allEmps]);

  const employeeFilter = (emp) => {
    return activeFilter(emp) && senatorFilter(emp) && payTypeFilter(emp);
  };

  const activeFilter = (emp) => {
    if (!activeOnly) return true;
    const today = new Date();
    const endDate = emp.effectiveEndDate ? new Date(emp.effectiveEndDate) : new Date(emp.supEndDate);
    return endDate >= today;
  };

  const senatorFilter = (emp) => {
    return showSenators || !emp.senator;
  };

  const payTypeFilter = (emp) => {
    if (!payType) return true;
    const payTypeRegex = new RegExp(payType, 'i');
    return payTypeRegex.test(emp.payType);
  };

  return (
    <div className={`${styles.employeeSelect} ${styles.contentContainer} ${styles.contentControls}`}>
      {validSupEmpGroupCount > 1 && (
        <p className={styles.contentInfo}>
          <span>
            View Employees Under Supervisor {'\u00A0'}
          </span>
          <span>
            <select value={iSelEmpGroup} onChange={(e) => setISelEmpGroup(Number(e.target.value))}>
              {supEmpGroups.map((group, index) => (
                <option key={index} value={index}>
                  {group.dropDownLabel}
                </option>
              ))}
            </select>
          </span>
        </p>
      )}
      <p className={styles.contentInfo}>
        <span>
          View {selectSubject} for Employee {'\u00A0'}
        </span>
        <span>
          <select value={iSelEmp} onChange={(e) => setISelEmp(Number(e.target.value))}>
            {allEmps.map((emp, index) => (
              <option key={index} value={index}>
                {emp.dropDownLabel}
              </option>
            ))}
          </select>
        </span>
      </p>
      {!loading && allEmps.length === 0 && (
        <div>
          {validSupEmpGroupCount > 1 ? (
            <div className={styles.essNotification} data-level="info">
              No valid Employee {selectSubject} can be viewed for the selected supervisor.
            </div>
          ) : (
             <div className={styles.essNotification} data-level="info">
               No valid Employee {selectSubject} are available for viewing.
             </div>
           )}
        </div>
      )}
    </div>
  );
};

export default EmployeeSelect;
