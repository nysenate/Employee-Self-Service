import React, { useState, useEffect } from "react";
import styles from "../universalStyles.module.css"
import Hero from "app/components/Hero";
import HistoryDirective from "app/views/time/accrual/HistoryDirective";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import useAuth from "app/contexts/Auth/useAuth";
import {
  fetchAccrualActiveYears,
  fetchAccrualSummaries,
  fetchEmployeeInfo
} from "app/views/time/accrual/time-accrual-ctrl";

const AccrualHistoryIndex = ({ empSupInfo }) => {
  // Scope Variables
  const auth = useAuth();
  const userId = auth.empId();
  const [accSummaries, setAccSummaries] = useState({});
  const [activeYears, setActiveYears] = useState([]);
  const [selectedYear, setSelectedYear] = useState(null);
  const [empInfo, setEmpInfo] = useState({});
  const [isTe, setIsTe] = useState(false);
  const [loading, setLoading] = useState({
    empInfo: false,
    empActiveYears: false,
    accSummaries: false,
  });
  const [error, setError] = useState(null);
  const empId = empSupInfo?.empId || userId;
  // Scope Fetch
  useEffect(() => {
    if (empId) {
      getEmpInfo();
      getEmpActiveYears();
    }
  }, [empId]);

  useEffect(() => {
    console.log('selectedYear: ', selectedYear);
    if (selectedYear) {
      getAccSummaries(selectedYear);
    }
  }, [selectedYear]);

  useEffect(()=> {
    console.log("accS: ", accSummaries);
  }, [accSummaries]);
  const getEmpInfo = async () => {
    setLoading((prev) => ({ ...prev, empInfo: true }));
    try {
      const response = await fetchEmployeeInfo({ empId, detail: true });

      const empInfo = response.employee;
      setEmpInfo(empInfo);
      setIsTe(empInfo.payType === 'TE');
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, empInfo: false }));
    }
  };
  const getEmpActiveYears = async () => {
    setLoading((prev) => ({ ...prev, empActiveYears: true }));
    try {
      const response = await fetchAccrualActiveYears({ empId });
      const years = response.years.reverse();
      setActiveYears(years);
      setSelectedYear(years.length > 0 ? years[0] : null);
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, empActiveYears: false }));
    }
  };
  const getAccSummaries = async (year) => {
    if (accSummaries[year]) return;
    console.log(year);
    setLoading((prev) => ({ ...prev, accSummaries: true }));
    try {
      const fromDate = new Date(Date.UTC(year, 0, 1)).toISOString().split('T')[0];
      const toDate = new Date(Date.UTC(year + 1, 0, 1)).toISOString().split('T')[0];
      const response = await fetchAccrualSummaries({ empId, fromDate, toDate });
      const sortedSummaries = response.result
        .filter(shouldDisplayRecord)
        .sort((a, b) => new Date(b.payPeriod.endDate) - new Date(a.payPeriod.endDate));

      setAccSummaries((prev) => ({
        ...prev,
        [year]: sortedSummaries,
      }));
      console.log(response);
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, accSummaries: false }));
    }
  };
  const handleErrorResponse = (error) => {
    setError({
      title: "Could not retrieve accrual information.",
      message: "If you are eligible for accruals please try again later.",
    });
    console.error(error);
  };
  const shouldDisplayRecord = (record) => {
    return (!record.computed || record.submitted) && record.empState?.payType !== 'TE';
  };
  // Scope Functions
  const isUser = () => {
    return empInfo.empId = userId;
  }
  const isLoading = () => {
    // console.log(Object.values(loading).some((status) => status));
    // console.log(loading);
    return Object.values(loading).some((status) => status);
  }
  const isEmpLoading = () => {
    return (loading.empInfo || loading.empActiveYears);
  }
  const clearAccSummaries = () => {
    setAccSummaries({});
  }

  // Connected Components' State Variables + setter/renderer functions
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedAccrual, setSelectedAccrual ] = useState(null);
  const viewDetails = (accrualRecord) => {
    console.log(accrualRecord);
    setSelectedAccrual(accrualRecord);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAccrual(null);
  }

  return (
    <div>
      <Hero>Accrual History</Hero>
      <HistoryDirective
        activeYears={activeYears}
        selectedYear={selectedYear}
        setSelectedYear={setSelectedYear}
        accSummaries={accSummaries}
        viewDetails={viewDetails}
        isUser={isUser}
        isLoading={isLoading}
        isEmpLoading={isEmpLoading}
        empSupInfo={empSupInfo}
      />
      {selectedAccrual && (
        <AccrualDetailsPopup
          accruals={selectedAccrual}
          isModalOpen={isModalOpen}
          closeModal={closeModal}
        />
      )}
    </div>
  );
}

export default AccrualHistoryIndex;