import React, { useState, useEffect } from 'react';
import styles from '../universalStyles.module.css';
import { fetchApiJson } from "app/utils/fetchJson";
import Hero from "app/components/Hero";

// 2018
const Calendar = () => {
  const currentYear = new Date().getFullYear();
  const [state, setState] = useState({
    year: currentYear,
    currentDay: new Date()
  });
  const [yearList, setYearList] = useState([]);
  const [months, setMonths] = useState([]);
  const [periods, setPeriods] = useState([]);
  const [periodMap, setPeriodMap] = useState({});
  const [holidays, setHolidays] = useState([]);
  const [holidayMap, setHolidayMap] = useState({});

  useEffect(() => {
    const years = Array.from({ length: 10 }, (_, y) => ((currentYear + 2) - y - 1));
    setYearList(years);

    const paramYear = new URLSearchParams(window.location.search).get('year');
    if (!isNaN(paramYear) && years.includes(parseInt(paramYear))) {
      setState(prevState => ({ ...prevState, year: parseInt(paramYear) }));
    }
  }, []);

  useEffect(() => {
    getPayPeriods(state.year, () => {
      getHolidays(state.year, () => {
        generateMonths(state.year);
      });
    });
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.set('year', state.year);
    window.history.replaceState(null, '', `?${searchParams.toString()}`);
  }, [state.year]);

  useEffect(() => {
    console.log(holidayMap);
  }, [holidayMap]);

  const getPayPeriods = async (year, callback) => {
    const periodResp = await fetchPayPeriodApi('AF', year);
    setPeriods(periodResp.periods);
    const periodMap = periodResp.periods.reduce((res, curr) => {
      res[curr.endDate] = curr;
      return res;
    }, {});
    setPeriodMap(periodMap);
    if (callback) callback();
  };

  const getHolidays = async (year, callback) => {
    const holidayResp = await fetchHolidayApi(year);
    setHolidays(holidayResp.holidays);
    const holidayMap = holidayResp.holidays.reduce((res, curr) => {
      res[curr.date] = curr;
      return res;
    }, {});
    setHolidayMap(holidayMap);
    if (callback) callback();
  };

  const generateMonths = (year) => {
    const months = [];
    for (let i = 0; i < 12; i++) {
      const date = new Date(year, i, 1);
      months.push(date.toLocaleDateString('en-US'));
    }
    setMonths(months);
  };

  const periodHighlight = (date) => {
    const cssClasses = [];
    const toolTips = [];
    const mDateStr = date.toISOString().split('T')[0];
    const currentDate = state.currentDay.toISOString().split('T')[0];

    if (mDateStr === currentDate) {
      cssClasses.push(styles.currentDate);
    }

    const day = date.getDay();
    if (day === 6 || day === 0) {
      cssClasses.push(styles.weekendDate);
    } else {
      if (holidayMap[mDateStr]) {
        toolTips.push(holidayMap[mDateStr]['name']);
        cssClasses.push(styles.holidayDate);
        const hours = holidayMap[mDateStr].hours;
        if (hours < 7) {
          toolTips.push(`${hours} hours holiday time`);
          cssClasses.push(styles.partialHolidayDate);
        }
      }
      if (periodMap[mDateStr] && !periodMap[mDateStr].endYearSplit) {
        toolTips.push(`Last Day of Pay Period ${periodMap[mDateStr]['payPeriodNum']}`);
        cssClasses.push(styles.payPeriodEndDate);
      }
    }
    return [false, cssClasses.join(' '), toolTips.join('\n')];
  };

  return (
    <div>
      <Hero>Payroll Calendar</Hero>
      <div className={`${styles.contentContainer} ${styles.contentControls}`}>
        <p className={styles.contentInfo}>
          Year {'\u00A0'}
          <select
            value={state.year}
            style={{color: "black", fontWeight: '400'}}
            onChange={(e) => setState({ ...state, year: parseInt(e.target.value) })}
          >
            {yearList.map((year) => (
              <option key={year} value={year}>{year}</option>
            ))}
          </select>
        </p>
      </div>

      <div className={`${styles.contentContainer} ${styles.payPeriodCalContainer}`}>
        <div className={`${styles.contentInfo} ${styles.legendContainer}`}>
          <div className={`${styles.legendBlock} ${styles.payPeriodCalPayPeriodEnd}`}>{'\u00A0'}</div>Pay Period End Date
          <div className={`${styles.legendBlock} ${styles.payPeriodCalHoliday}`}>{'\u00A0'}</div>Senate Holiday
          <div className={`${styles.legendBlock} ${styles.payPeriodCalPartialHoliday}`}>{'\u00A0'}</div>Partial Senate Holiday
        </div>
        <div className={styles.payPeriodCal}>
          {months.map((month, i) => {
            const date = new Date(month);
            const monthName = date.toLocaleString('default', { month: 'long' });
            const year = date.getFullYear();

            return (
              <div key={i} className={styles.payPeriodMonth}>
                <div
                  data-date={month}
                  className="datepicker"
                  // Implement the `periodHighlight` function as needed within your date picker component
                >
                  <div className={styles.uiDatepicker} style={{ display: "block" }}>
                    <div className={styles.uiDatepickerHeader}>
                      <div className={styles.uiDatepickerTitle}>
                        <span className={styles.uiDatepickerMonth}>{monthName}</span>
                        {'\u00A0'}
                        <span className={styles.uiDatepickerYear}>{year}</span>
                      </div>
                    </div>
                    <table className={styles.uiDatepicker}>
                      <thead>
                      <tr>
                        <th><span title="Sunday">Su</span></th>
                        <th><span title="Monday">Mo</span></th>
                        <th><span title="Tuesday">Tu</span></th>
                        <th><span title="Wednesday">We</span></th>
                        <th><span title="Thursday">Th</span></th>
                        <th><span title="Friday">Fr</span></th>
                        <th><span title="Saturday">Sa</span></th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default Calendar;

const fetchPayPeriodApi = async (periodType, selectedYear) => {
  return fetchApiJson(`/periods/${periodType}?year=${selectedYear}`, { method: 'GET' });
};

const fetchHolidayApi = async (selectedYear) => {
  return fetchApiJson(`/holidays?year=${selectedYear}`, { method: 'GET' });
};
