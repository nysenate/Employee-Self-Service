import React, { useEffect, useState } from "react";
import useAuth from "app/contexts/Auth/useAuth";
import { formatDateToMMDDYYYY, formatDateYYYYMMDD } from "app/views/time/helpers";
import { fetchApiJson } from "app/utils/fetchJson";
import Hero from "app/components/Hero";
import EssNotification from "app/components/EssNotification";
import LoadingIndicator from "app/components/LoadingIndicator";
import styles from "../universalStyles.module.css";
import { fetchChainData, fetchGrantData, fetchOverrideData } from "app/views/time/record/time-record-ctrl";
import { formatDateForInput } from "app/views/supply/helpers";

export default function Grant() {
  const { userData } = useAuth()
  const [state, setState] = useState({
    empId: userData().employee.employeeId || null,
    selectedGrantee: null,
    grantees: [],   // Stores an ordered list of the supervisors.
    granteeMap: {}, // Map of supId -> sup, allows easy modification of supervisor grant status.

    granters: [],    // List of overrides this supervisor has been granted

    modified: false,   // If the state has been altered.
    fetched: false,   // If the data has been fetched.
    saving: false,
    saved: false
  })

  useEffect(() => {
    if(!state.empId){
      console.log('no state.empId');
      return;
    }
    console.log('state.empId: ', state.empId);
    init();
  }, [state.empId]);

  const init = async () => {
    // Reset state before fetching data
    setState((prevState) => ({
      ...prevState,
      selectedGrantee: null,
      grantees: [],
      granteeMap: {},
      granters: [],
      modified: false,
      fetched: false,
      saving: false,
      saved: false,
    }));

    let tempState = {
      ...state,
      selectedGrantee: null,
      grantees: [],
      granteeMap: {},
      granters: [],
      modified: false,
      fetched: false,
      saving: false,
      saved: false,
    };

    fetchChain(tempState)
      .then((updatedTempState) => fetchGrants(updatedTempState))
      .then((updatedTempState) => fetchOverrides(updatedTempState))
      .then((updatedTempState) => setState(updatedTempState))
      .catch((err) => console.error(err));
  };
  // Fetch supervisor chain
  const fetchChain = async (tempState) => {
    if (!tempState.empId) return tempState;
    try {
      const response = await fetchChainData({ empId: tempState.empId }); // replace with actual API call
      response.result.supChain.forEach((sup) => {
        sup.granted = false;
        sup.grantStart = sup.grantEnd = null;
      });

      tempState = {
        ...tempState,
        grantees: [...tempState.grantees, ...response.result.supChain],
        granteeMap: {
          ...tempState.granteeMap,
          ...response.result.supChain.reduce((acc, sup) => {
            acc[sup.employeeId] = sup;
            return acc;
          }, {}),
        },
      };
      return tempState;
    } catch (err) {
      console.error(err);
      return tempState;
    }
  };
  // Link up with any existing grants
  const fetchGrants = async (updatedTempState) => {
    try {
      const response = await fetchGrantData({ supId: updatedTempState.empId }); // replace with actual API call
      let updatedGranteeMap = { ...updatedTempState.granteeMap };
      let updatedGrantees = [...updatedTempState.grantees];

      response.grants.forEach((grant) => {
        const supId = grant.granteeSupervisorId;

        if (!updatedGranteeMap[supId]) {
          updatedGranteeMap[supId] = {
            ...grant.granteeSupervisor,
            granted: true,
          };
          updatedGrantees = [...updatedGrantees, grant.granteeSupervisor];
        }
        updatedGranteeMap[supId].granted = true;
        updatedGranteeMap[supId].grantStart = grant.startDate ? formatDateToMMDDYYYY(grant.startDate) : null;
        updatedGranteeMap[supId].grantEnd = grant.endDate ? formatDateToMMDDYYYY(grant.endDate) : null;
      });

      updatedTempState = {
        ...updatedTempState,
        granteeMap: updatedGranteeMap,
        grantees: updatedGrantees,
      };
      return updatedTempState;
    } catch (err) {
      console.error(err);
      return updatedTempState;
    }
  };
  const fetchOverrides = async (updatedTempState) => {
    try {
      const response = await fetchOverrideData({ supId: updatedTempState.empId }); // replace with actual API call
      const updatedGranters = response.overrides
        .filter((ovr) => ovr.active)
        .map((ovr) => {
          let granter = ovr.overrideSupervisor;
          const startMoment = new Date(ovr.startDate || 0);
          const endMoment = new Date(ovr.endDate || '3000-01-01');
          granter.grantStartStr = ovr.startDate ? formatDateToMMDDYYYY(startMoment) : 'No Start Date';
          granter.grantEndStr = ovr.endDate ? formatDateToMMDDYYYY(endMoment) : 'No End Date';
          granter.status = ovr.active ? 'Active' : 'Inactive';

          const today = new Date();
          if (today < startMoment) {
            granter.status = 'Pending';
          } else if (today > endMoment) {
            granter.status = 'Expired';
          } else {
            granter.status = 'Active';
          }
          return granter;
        });

      updatedTempState = {
        ...updatedTempState,
        granters: updatedGranters,
        fetched: true,
      };
      return updatedTempState;
    } catch (err) {
      console.error(err);
      return updatedTempState;
    }
  };

  // Updater
  const saveGrants = async () => {
    if(!state.modified || !state.fetched) return;
    let modifiedGrantees = state.grantees.filter((grantee) => {
      return grantee.modified === true;
    }).map((grantee) => {
      return createGrantSaveView(grantee);
    });
    let tempState = { ...state, saving: true };

    const payload = modifiedGrantees;
    try {
      await fetchApiJson(`/supervisor/grants`, { method: 'POST', payload: payload });
      tempState = {
        ...tempState,
        saving: false,
        modified: false,
        saved: true,
      };
      setState(tempState);
    } catch (err) { console.error(err); }
  }

  const createGrantSaveView = (grantee) => {
    return {
      granteeSupervisorId: grantee.employeeId,
      active: grantee.granted,
      granterSupervisorId: state.empId,
      startDate: (grantee.grantStart) ? formatDateToMMDDYYYY(grantee.grantStart) : null,
      endDate: (grantee.grantEnd) ? formatDateToMMDDYYYY(grantee.grantEnd) : null,
    }
  }

  // Modifiers
  const setStartDate = (index, grantee) => {
    let granteeStart = null;
    if (!grantee.grantStart){
      const today = new Date();
      granteeStart = formatDateYYYYMMDD(today);
    }
    setModified(index, {...grantee, grantStart: granteeStart});
  }

  const setEndDate = (index, grantee) => {
    let granteeEnd = null;
    if (!grantee.grantEnd){
      const today = new Date();
      granteeEnd = formatDateYYYYMMDD(today);
    }
    setModified(index, {...grantee, grantEnd: granteeEnd});
  }

  const setModified = (index, grantee) => {
    setState((prevState) => {
      const updatedGrantees = [...prevState.grantees];
      updatedGrantees[index] = {
        ...grantee,
        modified: true,
      };

      return {
        ...prevState,
        modified: true,
        grantees: updatedGrantees,
      };
    });
  }

  const reset = () => {
    init();
  }
  useEffect(() => {
    console.log(state);
  }, [state]);

  const inlineDisabledStyles = {
    background: 'url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQIW2NkQAKrVq36zwjjgzhhYWGMYAEYB8RmROaABADeOQ8CXl/xfgAAAABJRU5ErkJggg==) repeat',
  };

  return (
    <>
      <Hero>Grant Supervisor Access</Hero>
      {state.fetched === true && state.grantees.length == 0 && (
        <EssNotification title={"No supervisor grants available."} level={'warn'}>
          <p>
            You do not have any supervisors that you can delegate your employee's record approvals to. Please contact Senate Personnel for more information.
          </p>
        </EssNotification>
      )}

      {state.fetched === false && (<LoadingIndicator/>)}

      <div className={`${styles.contentContainer} ${styles.contentControls}`}>
        {state.fetched === true && state.grantees.length > 0 && (
          <div>
            <p className={styles.contentInfo}>
              Grant another supervisor privileges to review and/or approve your direct employee's time records.
            </p>
            <div className={styles.paddingX}>
              <table className={styles.simpleTable}>
                <thead>
                <tr>
                  <th>#</th>
                  <th>Supervisor</th>
                  <th>Status</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                </tr>
                </thead>
                <tbody>
                {state.grantees.map((grantee, index) => (
                  <tr key={index}>
                    <td>{index + 1}</td>
                    <td>{`${grantee.firstName} ${grantee.lastName}`}</td>
                    <td>
                      <div className={styles.horizontalInputGroup}>
                        <input
                          type="checkbox"
                          checked={grantee.granted}
                          onChange={() => setModified(index, {...grantee, granted: !(grantee.granted) })}
                        />
                        <label
                          className={grantee.granted ? styles.successBoldLabel : ''}
                          htmlFor={`grant-status-yes-${index}`}
                        >
                          Grant Access
                        </label>
                      </div>
                    </td>
                    <td className={!grantee.granted ? styles.halfOpacity : ''}>
                      <div className={styles.horizontalInputGroup}>
                        <input
                          checked={grantee.grantStart}
                          disabled={!grantee.granted}
                          type="checkbox"
                          onChange={() => setStartDate(index, grantee)}
                        />
                        <label className={styles.bold}>Set Start Date </label>
                        <input
                          className={
                            !grantee.granted || !grantee.grantStart ? styles.halfOpacity : ''
                          }
                          disabled={!grantee.granted || !grantee.grantStart}
                          value={grantee.grantStart ? grantee.grantStart : ''}
                          onChange={(e) => setModified(index,{ ...grantee, grantStart: e.target.value })}
                          style={{ width: '100px' }}
                          type="date"
                        />
                      </div>
                    </td>
                    <td className={!grantee.granted ? styles.halfOpacity : ''}>
                      <div className={styles.horizontalInputGroup}>
                        <input
                          checked={grantee.grantEnd}
                          disabled={!grantee.granted}
                          type="checkbox"
                          onChange={() => setEndDate(index, grantee)}
                        />
                        <label htmlFor={`grant-end-date-${index}`}>Set End Date</label>
                        <input
                          key={index}
                          disabled={!grantee.granted}
                          value={grantee.grantEnd ? grantee.grantEnd : ''}
                          onChange={(e) => setModified(index, { ...grantee, grantEnd: e.target.value })}
                          style={{ width: '100px' }}
                          type="date"
                        />
                      </div>
                    </td>
                  </tr>
                ))}
                </tbody>
              </table>
              <hr/>
              {state.saving === true && <LoadingIndicator/>}
              {state.saved === true && !state.modified && (
                <EssNotification level="info" title="Grants have been updated." />
              )}
              <div className={styles.contentInfo} style={{ textAlign: 'center' }}>
                <input
                  type="button"
                  className={styles.timeNeutralButton}
                  disabled={!state.modified}
                  style={!state.modified ? { ...inlineDisabledStyles, ...{ marginRight: '3px' } } : {marginRight: '3px'}}
                  value="Discard Changes"
                  onClick={reset}
                />
                <input
                  type="button"
                  className={styles.submitButton}
                  disabled={!state.modified}
                  style={!state.modified ? inlineDisabledStyles : {}}
                  value="Change Supervisor Access"
                  onClick={saveGrants}
                />
              </div>
            </div>
          </div>
        )}
      </div>
      {state.granters.length > 0 && (
        <div className={`${styles.contentContainer} ${styles.contentControls}`}
             style={{ marginTop: '20px'}}
        >
          <p className={styles.contentInfo}>
            The following employees have granted privileges to you.
          </p>
          <div className={styles.paddingX}>
            <table className={styles.simpleTable}>
              <thead>
              <tr>
                <th>#</th>
                <th>Supervisor</th>
                <th>Status</th>
                <th>Start Date</th>
                <th>End Date</th>
              </tr>
              </thead>
              <tbody>
              {state.granters.map((granter, index) => (
                <tr key={index}>
                  <td>{index + 1}</td>
                  <td>{`${granter.firstName} ${granter.lastName}`}</td>
                  <td>{granter.status}</td>
                  <td>{granter.grantStartStr}</td>
                  <td>{granter.grantEndStr}</td>
                </tr>
              ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
      {/*  Modal Containter?? */}
    </>
  )
}

















