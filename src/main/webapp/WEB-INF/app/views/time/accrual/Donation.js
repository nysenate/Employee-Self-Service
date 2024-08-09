import styles from "../universalStyles.module.css";
import useAuth from "app/contexts/Auth/useAuth";
import React, { useEffect, useState } from "react";
import { fetchApiJson } from "app/utils/fetchJson";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import Popup from "app/components/Popup";
import { Button } from "app/components/Button";
import { fetchDonationHistory, fetchDonationInfo } from "app/views/time/accrual/time-accrual-ctrl";

export default function Donation() {
  const { userData } = useAuth();
  const [state, setState] = useState({
    empId: userData().employee.employeeId,
    realLastName: userData().employee.lastName,
    hoursToDonate: null,
    selectedYear: new Date().getFullYear(),
    maxDonation: null,
    accruedSickTime: null,
    donationData: [],
  });

  const [isContinuePopupOpen, setIsContinuePopupOpen] = useState(false);
  const [isConfirmPopupOpen, setIsConfirmPopupOpen] = useState(false);

  useEffect(() => {
    setDonationHistory();
  }, [state.selectedYear]);
  useEffect(() => {
    console.log(state);
  }, [state]);
  useEffect(() => {
    if (!state.empId) return;
    setDonationInfo();
  }, []);

  const setDonationInfo = async () => {
    setState((prevState) => ({
      ...prevState,
      maxDonation: null,
    }));
    const params = { empId: state.empId, };
    try {
      const response = await fetchDonationInfo(params);
      setState((prevState) => ({
        ...prevState,
        maxDonation: response.result.maxDonation,
        accruedSickTime: response.result.accruedSickTime,
      }));
    } catch (err) { console.error(err); }
  }
  const setDonationHistory = async () => {
    const params = {empId: state.empId, year: state.selectedYear};
    try {
      const response = await fetchDonationHistory(params);
      setState((prevState) => ({
        ...prevState,
        donationData: response.result,
      }));
    } catch(err) { console.error(err); }
  }
  const getYears = () => {
    const years = [];
    // Can't use 'let' for some reason
    for (let i = 2023; i <= new Date().getFullYear(); i++) {
      years.push(i);
    }
    return years;
  }
  const submitDonation = async () => {
    try {
      await fetchApiJson(`/donation/submit?empId=${state.empId}&hoursToDonate=${state.hoursToDonate}`, { method: 'POST', payload: {} });
      await setDonationInfo();
      await setDonationHistory();
    } catch (err) { console.error(err); } finally {
      setState((prevState) => ({
        ...prevState,
        hoursToDonate: null,
      }));
    }
  }
  const openContinuePopup = () => {
    setIsContinuePopupOpen(true);
  }
  const closeContinuePopup = () => {
    setIsContinuePopupOpen(false);
  }
  const openConfirmPopup = () => {
    setIsConfirmPopupOpen(true);
  }
  const closeConfirmPopup = () => {
    setIsConfirmPopupOpen(false);
  }

  const handleHoursToDonate = (hours) => {
    setState((prevState) => ({
      ...prevState,
      hoursToDonate: hours,
    }));
  }

  const inlineDisabledStyles = {
    background: 'url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQIW2NkQAKrVq36zwjjgzhhYWGMYAEYB8RmROaABADeOQ8CXl/xfgAAAABJRU5ErkJggg==) repeat',
  };

  return (
    <div>
      <Hero>Sick Leave Donation</Hero>

      <div style={{ textAlign: 'center', fontSize: "120%" }}>
        {state.maxDonation == null && (<LoadingIndicator/>)}

        {state.maxDonation != null && state.maxDonation == 0 &&
          (<p>You are ineligible to donate sick leave at this time.</p>)}

        {state.maxDonation >= 0.5 && (<div className={styles.contentContainer}>
          <p style={{ paddingTop: '10px' }}>
            You may donate up to {state.maxDonation} hours in half-hour increments.
          </p>
          <form>
            <label>Donation amount:</label>
            <input
              type="number"
              id="donation"
              name="donation"
              style={{ margin: '5px' }}
              min="0.5"
              max={ state.maxDonation }
              step="0.5"
              value={state.hoursToDonate}
              onChange={(e) => handleHoursToDonate(e.target.value)}
            />

            <input
              onClick={openContinuePopup}
              className={styles.submitButton}
              type="button"
              value="Continue"
              disabled={!state.hoursToDonate}
              style={!state.hoursToDonate ? inlineDisabledStyles : {}}
            />
          </form>
          <br/>
        </div>)}
      </div>

      <div className={styles.contentControls} style={{ padding: '10px' }}>
        <p className={styles.contentInfo} style={{ textAlign: 'left', padding: '0' }}>
          Filter By Year {'\u00A0'}
          <YearSelect
            years={getYears()}
            selectedYear={state.selectedYear}
            setYear={(set) =>
              setState((prevState) => ({
                ...prevState,
                selectedYear: set,
              }))
            }
          />
        </p>
        {state.donationData.length !== 0 && (<table className={styles.donationHistoryTable}>
          <thead>
          <tr>
            <th>Date</th>
            <th>Donation Amount</th>
          </tr>
          </thead>
          <tbody>
          {state.donationData.map((donation, index) => (
            <tr key={index}>
              <td>{ donation.split(":")[0] }</td>
              <td>{ donation.split(":")[1] }</td>
            </tr>
          ))}
          </tbody>
        </table>)}
        {state.donationData.length === 0 && (<div>
          You have no donations for this year yet.
        </div>)}
      </div>
      <DonationContinuePopup
        isModalOpen={isContinuePopupOpen}
        closeModal={closeContinuePopup}
        onAction={openConfirmPopup}
        state={state}
      />
      <DonationConfirmPopup
        isModalOpen={isConfirmPopupOpen}
        closeModal={closeConfirmPopup}
        onAction={submitDonation}
        state={state}
      />
    </div>
  );
}


const YearSelect = ({ years, selectedYear, setYear }) => {
  const handleChange = (event) => {
    setYear(parseInt(event.target.value, 10));
  };

  return (
    <select value={selectedYear} onChange={handleChange} style={{color: 'black', fontWeight: '400'}}>
      {years.map((year, index) => (
        <option key={index} value={year}>
          {year}
        </option>
      ))}
    </select>
  );
};


function DonationContinuePopup({ isModalOpen, closeModal, onAction, state }) {
  const handleCancel = () => {
    closeModal();
  };

  const handleYes = () => {
    onAction();
    closeModal();
  };

  return (
    <Popup
      isLocked={false}
      isOpen={isModalOpen}
      onClose={closeModal}
    >
      <div className={styles.confirmModal} style={{ fontSize: '120%'}}>
        <h3 className={styles.contentInfo}>
          Donation Confirmation
        </h3>
        <div className={styles.confirmationMessage}>
          You will donate {state.hoursToDonate} out of {state.accruedSickTime} accrued sick hours.
          <br/>
          <hr/>
          <div className={styles.inputContainer}>
            <input
              onClick={handleYes}
              className={styles.submitButton}
              type={"button"}
              value={"Continue"}
            />
            <input
              onClick={handleCancel}
              className={styles.rejectButton}
              type={"button"}
              value={"Go Back"}
            />
          </div>
        </div>
      </div>
    </Popup>
  );
}
function DonationConfirmPopup({ isModalOpen, closeModal, onAction, state }) {
  const handleCancel = () => {
    setInput('');
    closeModal();
  };

  const handleYes = () => {
    setInput('');
    onAction();
    closeModal();
  };

  const [input, setInput] = useState('');
  const inlineDisabledStyles = {
    background: 'url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQIW2NkQAKrVq36zwjjgzhhYWGMYAEYB8RmROaABADeOQ8CXl/xfgAAAABJRU5ErkJggg==) repeat',
  };

  return (
    <Popup
      isLocked={false}
      isOpen={isModalOpen}
      onClose={closeModal}
    >
      <div className={styles.confirmModal} style={{ fontSize: '120%'}}>
        <h3 className={styles.contentInfo}>
          Donation Confirmation
        </h3>
        <div className={styles.confirmationMessage}>
          <p>
            Once you donate your sick leave, it is irrevocable and forfeited permanently.<br/>
            The donated sick leave will not be returned to you.
          </p>
          <form className={styles.lastDonationConfirmForm}>
            <div>
              <label>Last Name</label>
              <span>{state.realLastName}</span>
            </div>
            <div style={{ marginBottom: '10px' }}>
              <label>Confirm Last Name</label>
              <input
                type="text"
                value={input}
                onChange={(e) => {setInput(e.target.value)}}
              />
            </div>
          </form>
          <hr />
          <div className={styles.inputContainer}>
            <input
              onClick={handleYes}
              disabled={input !== state.realLastName}
              style={input !== state.realLastName ? inlineDisabledStyles : {}}
              className={styles.submitButton}
              type={"button"}
              value={"Submit"}
            />
            <input
              onClick={handleCancel}
              disabled={input ===! state.realLastName}
              className={styles.rejectButton}
              type={"button"}
              value={"Cancel"}
            />
          </div>
        </div>
      </div>
    </Popup>
  );
}