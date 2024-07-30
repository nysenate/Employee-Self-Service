import { useState, useEffect, useCallback } from 'react';
import { fetchSupEmployeeApi, fetchSupOverrideApi } from './time-accrual-ctrl';
import useAuth from "app/contexts/Auth/useAuth";

let extendedSupEmpGroup = null;
let supEmpGroupList = [];
let nameMap = {};
let supIdMap = {};

const compareStrings = (a, b) => {
  const personA = a.empLastName.toUpperCase();
  const personB = b.empLastName.toUpperCase();
  return personA.localeCompare(personB);
};

const getTier = (empId) => {
  const topSupId = extendedSupEmpGroup.supId;
  const visitedSupIds = {};
  visitedSupIds[topSupId] = true;

  let currentEmpId = empId;
  let tier = 0;

  while (currentEmpId && !visitedSupIds[currentEmpId]) {
    tier++;
    visitedSupIds[currentEmpId] = true;
    currentEmpId = supIdMap[currentEmpId];
  }

  return tier;
};

const setEmpMaps = (userId, user) => {
  const primaryEmpInfos = extendedSupEmpGroup.primaryEmployees;
  const empOverrideInfos = extendedSupEmpGroup.empOverrideEmployees;
  const supOverrideInfos = Object.keys(extendedSupEmpGroup.supOverrideEmployees).map(k => extendedSupEmpGroup.supOverrideEmployees[k]);

  let allEmpInfos = primaryEmpInfos.concat(empOverrideInfos).concat(supOverrideInfos);

  const empSupEmpGroupMap = extendedSupEmpGroup.employeeSupEmpGroups;
  Object.values(empSupEmpGroupMap).forEach(supEmpGroups => {
    supEmpGroups.forEach(supEmpGroup => {
      allEmpInfos = allEmpInfos.concat(supEmpGroup.primaryEmployees);
    });
  });

  allEmpInfos.forEach(empInfo => {
    nameMap[empInfo.empId] = {
      firstName: empInfo.empFirstName,
      lastName: empInfo.empLastName,
      fullName: `${empInfo.empFirstName} ${empInfo.empLastName}`
    };

    if (!empInfo.empOverride) {
      supIdMap[empInfo.empId] = empInfo.supId;
    }
  });

  nameMap[userId] = {
    firstName: user.firstName,
    lastName: user.lastName,
    fullName: `${user.firstName} ${user.lastName}`
  };
};

const setSupEmpGroups = () => {
  const empSupEmpGroups = [];

  Object.values(extendedSupEmpGroup.employeeSupEmpGroups).forEach(supEmpGroups => {
    supEmpGroups.forEach(empGroup => {
      empGroup.supStartDate = empGroup.effectiveFrom;
      empGroup.supEndDate = empGroup.effectiveTo;
      empGroup.empFirstName = nameMap[empGroup.supId].firstName;
      empGroup.empLastName = nameMap[empGroup.supId].lastName;
      empSupEmpGroups.push(empGroup);
    });
  });

  empSupEmpGroups.sort((a, b) => {
    const tierA = getTier(a.supId);
    const tierB = getTier(b.supId);
    if (tierA !== tierB) return tierA - tierB;
    if (a.empLastName !== b.empLastName) return a.empLastName.localeCompare(b.empLastName);
    if (a.empFirstName !== b.empFirstName) return a.empFirstName.localeCompare(b.empFirstName);
    return a.supId - b.supId;
  });

  supEmpGroupList = [extendedSupEmpGroup].concat(empSupEmpGroups);
};

export const useSupEmpGroupService = () => {
  const [loading, setLoading] = useState(false);
  const { userData, empId } = useAuth();
  const userId = empId();

  const init = useCallback(async () => {
    if (extendedSupEmpGroup) {
      return;
    }
    setLoading(true);
    try {
      await Promise.all([loadSupEmpGroup(), loadSupOverrides()]);
    } catch (error) {
      console.error('Initialization error:', error);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  const loadSupEmpGroup = useCallback(async () => {
    const fromDate = new Date();
    fromDate.setFullYear(fromDate.getFullYear() - 2);
    const fromDateString = fromDate.toISOString().split('T')[0];

    const params = {
      supId: userId,
      fromDate: fromDateString,
      extended: true
    };

    const response = await fetchSupEmployeeApi(params);
    // console.log("fetchSupEmployeeApi", response);
    extendedSupEmpGroup = response.result;
    setEmpMaps(userId, userData().employee);
    setSupEmpGroups();
  }, [userId]);

  const loadSupOverrides = useCallback(async () => {
    const params = { supId: userId };
    const response = await fetchSupOverrideApi(params);

    response.overrides.forEach(override => {
      const ovrEmpId = override.overrideSupervisorId;
      const ovrEmpInfo = override.overrideSupervisor;

      nameMap[ovrEmpInfo.employeeId] = {
        firstName: ovrEmpInfo.firstName,
        lastName: ovrEmpInfo.lastName,
        fullName: ovrEmpInfo.fullName
      };

      supIdMap[ovrEmpId] = override.supervisorId;
    });
  }, [userId]);

  useEffect(() => {
    init();
  }, [init]);

  const getEmpInfos = useCallback((iSupEmpGroup, omitSenators) => {
    if (iSupEmpGroup < 0 || iSupEmpGroup >= supEmpGroupList.length) {
      throw new Error("sup emp group index out of bounds: " + iSupEmpGroup);
    }
    const selEmpGroup = supEmpGroupList[iSupEmpGroup];
    const isUser = selEmpGroup.supId === extendedSupEmpGroup.supId;
    let empList = [];

    const primaryEmps = selEmpGroup.primaryEmployees.sort(compareStrings);
    empList = empList.concat(primaryEmps);

    if (isUser) {
      selEmpGroup.empOverrideEmployees.forEach(emp => {
        emp.empOverride = true; //FIX::: this attribute needs to be inserted (does not exist prior)
        empList.push(emp);
      });
      Object.values(selEmpGroup.supOverrideEmployees.items).forEach(supGroup => {
        supGroup.forEach(emp => {
          emp.supOverride = true; //FIX::: this attribute needs to be inserted (does not exist prior)
          empList.push(emp);
        });
      });
    }

    return empList.filter(emp => !(omitSenators && emp.senator));
  }, []);

  const getSupEmpGroupList = useCallback(() => supEmpGroupList, []);
  const getName = useCallback(empId => nameMap[empId], []);
  const getSupId = useCallback(empId => supIdMap[empId], []);
  const getTier = useCallback(empId => getTier(empId), []);

  return {
    loading,
    getEmpInfos,
    getSupEmpGroupList,
    getName,
    getSupId,
    getTier
  };
};
