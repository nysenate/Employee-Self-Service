import React, { useMemo } from "react";
import Calendar from "react-calendar";
import { useSearchParams } from "react-router-dom";
import { format } from "date-fns";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Controls from "app/components/Controls";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useHoliday } from "app/views/time/useHoliday";
import { usePayPeriods } from "app/views/time/usePayPeriod";
import styles from "./calendar.module.css";
import "react-calendar/dist/Calendar.css";

// The calendar displays affirmation pay periods.
const PAY_PERIOD_TYPE = "AF";

// Holidays granting fewer hours than this are only partial holidays.
const FULL_HOLIDAY_HOURS = 7;

export default function PayrollCalendar() {
  const currentYear = new Date().getFullYear();
  const yearList = useMemo(() => getYearList(currentYear), [currentYear]);
  const [year, setYear] = useSelectedYear(currentYear, yearList);

  const holidays = useHoliday(year);
  const payPeriods = usePayPeriods(PAY_PERIOD_TYPE, year);

  return (
    <div>
      <Hero>Payroll Calendar</Hero>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="year">
          Year&nbsp;
        </label>
        <select
          id="year"
          name="year"
          className="select"
          value={year}
          onChange={(e) => setYear(parseInt(e.target.value))}
        >
          {yearList.map((yearOption) => (
            <option value={yearOption} key={yearOption}>
              {yearOption}
            </option>
          ))}
        </select>
      </Controls>

      <Card className="mt-3 p-3">
        <Legend />
      </Card>

      {holidays.isPending || payPeriods.isPending ? (
        <LoadingIndicator />
      ) : (
        <Card className="mt-3 p-3">
          <YearCalendar
            year={year}
            holidays={holidays.data}
            payPeriods={payPeriods.data}
          />
        </Card>
      )}
    </div>
  );
}

/**
 * Keeps the selected year in sync with the "year" search param so the page can be linked to.
 * The param is dropped when the current year is selected.
 */
function useSelectedYear(currentYear, yearList) {
  const [searchParams, setSearchParams] = useSearchParams();

  const paramYear = parseInt(searchParams.get("year"));
  const year = yearList.includes(paramYear) ? paramYear : currentYear;

  const setYear = (nextYear) => {
    setSearchParams(
      (params) => {
        if (nextYear === currentYear) {
          params.delete("year");
        } else {
          params.set("year", nextYear);
        }
        return params;
      },
      { replace: true },
    );
  };

  return [year, setYear];
}

/** Ten selectable years, from next year back through the previous eight. */
function getYearList(currentYear) {
  return Array.from({ length: 10 }, (value, index) => currentYear + 1 - index);
}

function Legend() {
  return (
    <div className="flex flex-wrap items-center justify-center gap-x-6 gap-y-2 text-gray-500">
      <LegendItem className={styles.payPeriodEndLegend}>
        Pay Period End Date
      </LegendItem>
      <LegendItem className={styles.holidayLegend}>Senate Holiday</LegendItem>
      <LegendItem className={styles.partialHolidayLegend}>
        Partial Senate Holiday
      </LegendItem>
    </div>
  );
}

function LegendItem({ className, children }) {
  return (
    <span className="flex items-center gap-2">
      <span className={`inline-block h-5 w-6 ${className}`} />
      {children}
    </span>
  );
}

function YearCalendar({ year, holidays, payPeriods }) {
  const months = useMemo(
    () =>
      Array.from({ length: 12 }, (value, month) => new Date(year, month, 1)),
    [year],
  );

  /*
   * The twelve month calendars render around 365 tiles between them, and each tile asks
   * for its classes and its tooltip separately. Rather than deriving both from scratch on
   * every tile of every render, the highlighted dates for the whole year are resolved once
   * and looked up by a numeric date key.
   */
  const dateInfo = useMemo(
    () => buildDateInfo(year, holidays, payPeriods),
    [year, holidays, payPeriods],
  );

  const getTileInfo = (date) => dateInfo.get(getDateKey(date)) || UNMARKED_DATE;

  return (
    <div className="grid grid-cols-3 gap-x-5 gap-y-6">
      {months.map((month) => (
        <div className="w-full" key={month.getTime()}>
          <div className="text-center text-gray-600">
            <span className="font-bold">
              {month.toLocaleString("default", { month: "long" })}
            </span>{" "}
            {month.getFullYear()}
          </div>
          <Calendar
            className={styles.monthCalendar}
            formatShortWeekday={formatShortWeekday}
            activeStartDate={month}
            calendarType="gregory"
            showNeighboringMonth={false}
            showNavigation={false}
            tileDisabled={alwaysDisabled}
            tileClassName={({ date, view }) =>
              view === "month" ? getTileInfo(date).classNames : null
            }
            tileContent={({ date, view }) => {
              if (view !== "month") {
                return null;
              }
              const { tooltip } = getTileInfo(date);
              return tooltip ? (
                <span className={styles.tileTooltip} title={tooltip} />
              ) : null;
            }}
          />
        </div>
      ))}
    </div>
  );
}

/** Two letter weekday headings, i.e. "Su", matching the legacy datepickers. */
function formatShortWeekday(locale, date) {
  return date
    .toLocaleString(locale || "default", { weekday: "short" })
    .slice(0, 2);
}

/** Shared result for the majority of dates, which carry no highlighting at all. */
const UNMARKED_DATE = { classNames: null, tooltip: "" };

/** The calendar is display only, so no date is ever selectable. */
const alwaysDisabled = () => true;

/** A sortable numeric key for a date, i.e. 20260724. Cheaper than formatting a string. */
function getDateKey(date) {
  return (
    date.getFullYear() * 10000 + (date.getMonth() + 1) * 100 + date.getDate()
  );
}

/**
 * Walks every day of the year once and records the highlighting for the dates that need it.
 * Returns a Map of date key to { classNames, tooltip }.
 */
function buildDateInfo(year, holidays, payPeriods) {
  const holidayMap = keyBy(holidays, "date");
  const payPeriodMap = keyBy(payPeriods, "endDate");
  const today = format(new Date(), "yyyy-MM-dd");
  const dateInfo = new Map();

  const date = new Date(year, 0, 1);
  while (date.getFullYear() === year) {
    const info = describeDate(date, { holidayMap, payPeriodMap, today });
    if (info.classNames.length > 0 || info.tooltip) {
      dateInfo.set(getDateKey(date), info);
    }
    date.setDate(date.getDate() + 1);
  }

  return dateInfo;
}

/**
 * Determines the highlight classes and tooltip text for a single date on the calendar.
 * Weekends are never marked as holidays or pay period end dates, matching the legacy page.
 */
function describeDate(date, { holidayMap, payPeriodMap, today }) {
  const classNames = [];
  const tooltips = [];
  const dateStr = format(date, "yyyy-MM-dd");
  const dayOfWeek = date.getDay();

  if (dateStr === today) {
    classNames.push(styles.currentDate);
  }

  if (dayOfWeek === 0 || dayOfWeek === 6) {
    classNames.push(styles.weekendDate);
    return { classNames, tooltip: "" };
  }

  const holiday = holidayMap[dateStr];
  if (holiday) {
    tooltips.push(holiday.name);
    classNames.push(styles.holidayDate);
    if (holiday.hours < FULL_HOLIDAY_HOURS) {
      tooltips.push(`${holiday.hours} hours holiday time`);
      classNames.push(styles.partialHolidayDate);
    }
  }

  const payPeriod = payPeriodMap[dateStr];
  if (payPeriod && !payPeriod.endYearSplit) {
    tooltips.push(`Last Day of Pay Period ${payPeriod.payPeriodNum}`);
    classNames.push(styles.payPeriodEndDate);
  }

  return { classNames, tooltip: tooltips.join("\n") };
}

function keyBy(items, key) {
  return (items || []).reduce((map, item) => {
    map[item[key]] = item;
    return map;
  }, {});
}
