import React, { useState } from "react";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import Controls from "app/components/Controls";
import Card from "app/components/Card";
import Calendar from "react-calendar";
import styles from "./calendar.module.css";
import "react-calendar/dist/Calendar.css";

export default function PayrollCalendar() {
  let currentYear = new Date().getFullYear();
  let currentMonth = new Date().getMonth();
  let currentDay = new Date().getDay();

  const [year, setYear] = useState(currentYear);
  const [month, setMonth] = useState(currentMonth);
  const [day, setDay] = useState(currentDay);

  let yearList = Array.apply(0, Array(10)).map(function (x, y) {
    return currentYear + 1 - y - 1;
  });
  let months = [];
  generateMonths(year);

  let periods = [];
  let periodMap = "";
  let holidays = "";
  let holidayMap = "";
  1;

  function changeYear(event) {
    let year = event.target.value;
    console.log(year);
    setYear(year);
    generateMonths(year);
    // getHolidays(year);
    // getPayPeriods(year);
  }

  function getHolidays(year) {
    let holidayResp = getHolidays(year);
    holidays = holidayResp.holidays;
    holidayMap = holidays.reduce(function (res, curr) {
      res[curr.date] = curr;
      return res;
    }, {});
  }

  function getPayPeriods(year) {
    let periodResp = getPayPeriods("AF", year);
    periods = periodResp.periods;
    periodMap = periods.reduce(function (res, curr) {
      res[curr.endDate] = curr;
      return res;
    }, {});
  }

  function generateMonths(year) {
    for (var i = 0; i < 12; i++) {
      months.push(new Date(year, i, 1)); //moment().year(year).month(i).format("M/D/YYYY")
    }
  }

  const listYears = yearList.map((individualYear) => (
    <option value={individualYear} key={individualYear}>
      {individualYear}
    </option>
  ));

  const calendar = months.map((individualMonth) => (
    <div
      className="inline-block justify-center align-middle"
      key={individualMonth}
    >
      <div className="text-center font-semibold text-teal-700">
        {individualMonth.toLocaleString("default", { month: "long" })}
      </div>
      <Calendar
        // className={styles.reactCalendar}
        defaultActiveStartDate={individualMonth}
        calendarType={"gregory"}
        showNeighboringCentury={false}
        showNeighboringDecade={false}
        showNeighboringMonth={false}
        showNavigation={false}
      />
    </div>
  ));

  return (
    <>
      <Hero>Payroll Calendar</Hero>
      <Controls className="p-4">
        <div className="text-center font-semibold text-teal-700">
          Year
          <select
            className="inline-block justify-center rounded-lg border border-gray-50 bg-gray-50 p-2.5 text-sm focus:border-blue-50 focus:ring-blue-50"
            onChange={changeYear}
          >
            {listYears}
          </select>
        </div>
      </Controls>

      <Card className="mt-0.5 p-4">
        <div className="flex justify-center align-middle font-semibold text-teal-700">
          <div className="margin-right-10 inline-block h-6 w-6 bg-yellow-500"></div>
          &nbsp;Pay Period End Date &nbsp;
          <div className="margin-right-10 inline-block h-6 w-6 bg-cyan-500"></div>
          &nbsp;Senate Holiday &nbsp;
          <div
            className="margin-right-10 inline-block h-6 w-6"
            style={{
              background:
                "repeating-linear-gradient(45deg,#D4FF60,#D4FF60 3px,#FAFAFA 3px,#FAFAFA 6px)",
            }}
          ></div>
          &nbsp;Partial Senate Holiday &nbsp;
        </div>
      </Card>

      <Card className="mt-0.5 flex flex-wrap justify-evenly gap-4 p-4">
        {calendar}
      </Card>
    </>
  );
}
