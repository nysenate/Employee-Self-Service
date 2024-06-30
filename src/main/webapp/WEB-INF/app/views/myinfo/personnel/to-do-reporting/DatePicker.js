import React, { useState } from 'react';

export default function DatePicker() {

  const [currentDate, setCurrentDate] = useState(new Date());

  const handlePrevMonth = () => {
    const prevMonth = new Date(currentDate);
    prevMonth.setMonth(prevMonth.getMonth() - 1);
    setCurrentDate(prevMonth);
  };

  const handleNextMonth = () => {
    const nextMonth = new Date(currentDate);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    setCurrentDate(nextMonth);
  };

  const handleDateChange = (e) => {
    const selectedDate = new Date(e.target.value);
    setCurrentDate(selectedDate);
  };

  const months = [
    'January', 'February', 'March', 'April',
    'May', 'June', 'July', 'August',
    'September', 'October', 'November', 'December'
  ];

  const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    return new Date(year, month + 1, 0).getDate();
  };

  const getFirstDayOfMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    return new Date(year, month, 1).getDay();
  };

  const totalDays = getDaysInMonth(currentDate);
  const firstDayOfMonth = getFirstDayOfMonth(currentDate);

  const weeks = [];
  let days = [];

  for (let i = 0; i < firstDayOfMonth; i++) {
    days.push('');
  }

  for (let day = 1; day <= totalDays; day++) {
    days.push(day);
    if (days.length === 7) {
      weeks.push(days);
      days = [];
    }
  }

  if (days.length > 0) {
    weeks.push(days);
  }

  return (
    <div className="calendar-with-input-container"> {/* Optional: you can style this container */}
      <div className="calendar-header">
        <button onClick={handlePrevMonth}>Prev</button>
        <h2>{months[currentDate.getMonth()]} {currentDate.getFullYear()}</h2>
        <button onClick={handleNextMonth}>Next</button>
      </div>
      <input
        type="date"
        value={currentDate.toISOString().split('T')[0]} // Format date as YYYY-MM-DD
        onChange={handleDateChange}
      />
      <table className="calendar-table">
        <thead>
        <tr>
          {daysOfWeek.map(day => (
            <th key={day}>{day}</th>
          ))}
        </tr>
        </thead>
        <tbody>
        {weeks.map((week, index) => (
          <tr key={index}>
            {week.map((day, index) => (
              <td key={index}>{day}</td>
            ))}
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  );
};
