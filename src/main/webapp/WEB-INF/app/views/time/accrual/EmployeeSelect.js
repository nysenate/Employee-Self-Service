import React, { useState, useEffect } from 'react';
import styles from "app/views/time/universalStyles.module.css";
import { useSupEmpGroupService } from '../accrual/supEmpGroupService';
import useAuth from "app/contexts/Auth/useAuth";

// Issues:
//         Additional Employees for a supervisor with only one employee which is weird
const EmployeeSelect = ({ setSelectedEmp, activeOnly = false, showSenators = false, payType, selectSubject = 'info' }) => {
  const { userData } = useAuth();
  const [iSelEmpGroup, setISelEmpGroup] = useState(-1);
  const [iSelEmp, setISelEmp] = useState(-1);
  const { loading, getEmpInfos, getSupEmpGroupList, getName } = useSupEmpGroupService();
  const supEmpGroups = getSupEmpGroupList();
  const [allEmps, setAllEmps] = useState([]);
  const validSupEmpGroupCount = supEmpGroups.length;

  const [updatedSupEmpGroups, setUpdatedSupEmpGroups] = useState([]);
  const [updatedAllEmps, setUpdatedAllEmps] = useState([]);

  useEffect(() => {
    console.log(validSupEmpGroupCount);
    if (iSelEmpGroup >= 0) {
      const emps = getEmpInfos(iSelEmpGroup, !showSenators);
      console.log("EmployeeSelect>useEffect>emps: ", emps);
      // const filteredEmps = emps.filter(emp => employeeFilter(emp));
      setAllEmps(emps);
      console.log("EmployeeSelect>useEffect>supEmpGroups: ", supEmpGroups);
      if (iSelEmp === 0) {
        // setSelectedEmp(filteredEmps[0]);
        setSelectedEmp(emps[0]);
      } else {
        setISelEmp(0);
      }
    }
  }, [iSelEmpGroup]); // This doesnt auto set the setSelectedEmp

  useEffect(() => {
    if (iSelEmp >= 0) {
      setSelectedEmp(allEmps[iSelEmp]);
    }
  }, [iSelEmp]);

  useEffect(() => {
    console.log("supEmpGroups: ", supEmpGroups);
    setUpdatedSupEmpGroups(setSupGroupLabels)
  }, [supEmpGroups]);
  useEffect(() => {
    console.log("updatedSupEmpGroups: ", updatedSupEmpGroups);
    if(validSupEmpGroupCount > 0) setISelEmpGroup(0);
  }, [updatedSupEmpGroups]);

  useEffect(() => {
    console.log("allEmps: ", allEmps);
    setUpdatedAllEmps(setEmpLabels)
  }, [allEmps]);
  useEffect(() => {
    console.log("updatedAllEmps: ", updatedAllEmps);
  }, [updatedAllEmps]);

  // const employeeFilter = (emp) => {
  //   return activeFilter(emp) && senatorFilter(emp) && payTypeFilter(emp);
  // };
  // const activeFilter = (emp) => {
  //   if (!activeOnly) return true;
  //   const today = new Date();
  //   const endDate = emp.effectiveEndDate ? new Date(emp.effectiveEndDate) : new Date(emp.supEndDate);
  //   return endDate >= today;
  // };
  // const senatorFilter = (emp) => {
  //   return showSenators || !emp.senator;
  // };
  // const payTypeFilter = (emp) => {
  //   if (!payType) return true;
  //   const payTypeRegex = new RegExp(payType, 'i');
  //   return payTypeRegex.test(emp.payType);
  // };

  const setSupGroupLabels = () => {
    return supEmpGroups.map(empGroup => {
      let user = userData().employee;
      if (empGroup.supId === user.employeeId) {
        const supName = getName(empGroup.supId);
        return { ...empGroup, dropDownLabel: supName.fullName };
      } else {
        const supSupId = empGroup.supSupId;
        const supSupName = getName(supSupId);
        return setDropDownLabel({ ...empGroup, group: 'Supervisors Under ' + supSupName.fullName });
      }
    });
  };
  const setEmpLabels = () => {
    return allEmps.map(emp => {
      let group;
      if (emp.empOverride) {
        group = 'Employee Overrides';
      } else if (emp.supOverride) {
        const supName = getName(emp.supId);
        group = (supName && supName.lastName)
                ? `${supName.lastName}'s Employees`
                : 'Sup Override Employees';
      } else {
        group = 'Direct Employees';
      }
      return setDropDownLabel({ ...emp, group: group }, emp.empOverride || emp.supOverride);
    });
  };
  const setDropDownLabel = (emp, override) => {
    const startDate = new Date(override ? emp.effectiveStartDate : emp.supStartDate || '1970-01-01');
    const endDate = new Date(override ? emp.effectiveEndDate : emp.supEndDate || '2999-12-31');

    // const name = emp.empLastName + ' ' + emp.empFirstName[0] + '.';
    const name = emp.empLastName + ' ' + emp.empFirstName + '.'; //Change to initial

    const formatDate = (date) => {
      const options = { year: 'numeric', month: 'short' };
      return date.toLocaleDateString('en-US', options);
    };

    let dates = formatDate(startDate);

    const today = new Date();

    if (endDate >= today) {
      dates += ' - Present';
    } else if (startDate < endDate) {
      dates += ' - ' + formatDate(endDate);
    }

    return { ...emp, dropDownLabel: name + ' (' + dates + ')' };
  };


  return (
    <div className={`${styles.employeeSelect} ${styles.contentContainer} ${styles.contentControls}`}>
      {validSupEmpGroupCount > 1 && (
        <p className={styles.contentInfo}>
          <span>
            View Employees Under Supervisor {'\u00A0'}
          </span>
          <span>
            <select
              value={iSelEmpGroup}
              onChange={(e) => setISelEmpGroup(Number(e.target.value))}
              style={{ color: 'black', fontWeight: '400'}}
            >
              {updatedSupEmpGroups.map((group, index) => (
                <option key={group.supId} value={index}>
                  {group.dropDownLabel || `${group.empLastName} ${group.empFirstName}`}
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
          <select
            value={iSelEmp}
            onChange={(e) => setISelEmp(Number(e.target.value))}
            style={{ color: 'black', fontWeight: '400'}}
          >
            {updatedAllEmps.map((emp, index) => (
              <option key={emp.empId} value={index}>
                {emp.dropDownLabel || `${emp.empLastName} ${emp.empFirstName}`}
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
