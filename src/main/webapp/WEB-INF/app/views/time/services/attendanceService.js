// services/attendanceService.js

export const fetchAccrualRecords = async (year) => {
  // Simulate an API delay
  await new Promise((resolve) => setTimeout(resolve, 500));

  // Hardcoded data for testing
  const data = [
    {
      period: "04",
      endDate: "05/22/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 264.5, avail: 229.5 },
      sick: { accrued: 3.5, used: 0, usedYTD: 38.5, avail: 271.5 }
    },
    {
      period: "03",
      endDate: "05/08/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 259, avail: 224 },
      sick: { accrued: 3.5, used: 7, usedYTD: 35, avail: 268 }
    },
    {
      period: "02",
      endDate: "04/24/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 253.5, avail: 218.5 },
      sick: { accrued: 3.5, used: 3, usedYTD: 35.5, avail: 271.5 }
    },
    {
      period: "01",
      endDate: "04/10/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 248, avail: 213 },
      sick: { accrued: 3.5, used: 1.5, usedYTD: 34.5, avail: 270 }
    },
    {
      period: "26",
      endDate: "03/27/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 242.5, avail: 221.5 },
      sick: { accrued: 3.5, used: 2.5, usedYTD: 34.5, avail: 266.5 }
    },
    {
      period: "25",
      endDate: "03/13/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 237, avail: 216 },
      sick: { accrued: 3.5, used: 1.5, usedYTD: 31.5, avail: 264.5 }
    },
    {
      period: "24",
      endDate: "02/28/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 229, avail: 210.5 },
      sick: { accrued: 3.5, used: 17.5, usedYTD: 28, avail: 253 }
    },
    {
      period: "23",
      endDate: "02/14/2024",
      personal: { accrued: 35, used: 0, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 226, avail: 219 },
      sick: { accrued: 3.5, used: 14, usedYTD: 25.5, avail: 270 }
    },
    {
      period: "22",
      endDate: "01/31/2024",
      personal: { accrued: 35, used: 2, usedYTD: 3.5, avail: 31.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 220.5, avail: 220.5 },
      sick: { accrued: 3.5, used: 10.5, usedYTD: 16, avail: 266.5 }
    },
    {
      period: "21",
      endDate: "01/17/2024",
      personal: { accrued: 35, used: 1.5, usedYTD: 1.5, avail: 33.5 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 215, avail: 216 },
      sick: { accrued: 3.5, used: 7, usedYTD: 0, avail: 269 }
    },
    {
      period: "20B",
      endDate: "01/03/2024",
      personal: { accrued: 35, used: 0, usedYTD: 0, avail: 35 },
      vacation: { accrued: 5.5, used: 0, usedYTD: 209.5, avail: 209.5 },
      sick: { accrued: 3.5, used: 3.5, usedYTD: 7, avail: 265.5 }
    }
  ];

  return data;
};


export const fetchAttendanceRecords = async (year) => {
    // Simulate an API delay
    await new Promise((resolve) => setTimeout(resolve, 500));
  
    // Hardcoded data for testing
    const data = {
      active: [
        {
          dateRange: '5/23/2024 - 6/5/2024',
          payPeriod: '05',
          status: 'Not Submitted',
          work: 19,
          holiday: 7,
          vacation: 2,
          personal: 1.5,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 29.5,
        },
      ],
      submitted: [
        {
          dateRange: '5/9/2024 - 5/22/2024',
          payPeriod: '04',
          status: 'Personnel Approved',
          work: 70,
          holiday: 0,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '4/25/2024 - 5/8/2024',
          payPeriod: '03',
          status: 'Personnel Approved',
          work: 63.5,
          holiday: 0,
          vacation: 0,
          personal: 7,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70.5,
        },
        {
          dateRange: '4/11/2024 - 4/24/2024',
          payPeriod: '02',
          status: 'Personnel Approved',
          work: 68,
          holiday: 0,
          vacation: 0,
          personal: 2,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '3/28/2024 - 4/10/2024',
          payPeriod: '01',
          status: 'Personnel Approved',
          work: 49,
          holiday: 14,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '3/14/2024 - 3/27/2024',
          payPeriod: '26',
          status: 'Personnel Approved',
          work: 69,
          holiday: 0,
          vacation: 0,
          personal: 1.5,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70.5,
        },
        {
          dateRange: '2/29/2024 - 3/13/2024',
          payPeriod: '25',
          status: 'Personnel Approved',
          work: 66.5,
          holiday: 0,
          vacation: 0,
          personal: 1.5,
          sickEmp: 2,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '2/15/2024 - 2/28/2024',
          payPeriod: '24',
          status: 'Personnel Approved',
          work: 40.5,
          holiday: 7,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70.5,
        },
        {
          dateRange: '2/1/2024 - 2/14/2024',
          payPeriod: '23',
          status: 'Personnel Approved',
          work: 63,
          holiday: 7,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '1/18/2024 - 1/31/2024',
          payPeriod: '22',
          status: 'Personnel Approved',
          work: 62,
          holiday: 0,
          vacation: 0,
          personal: 3,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '1/4/2024 - 1/17/2024',
          payPeriod: '21',
          status: 'Personnel Approved',
          work: 61.5,
          holiday: 7,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 70,
        },
        {
          dateRange: '1/1/2024 - 1/3/2024',
          payPeriod: '20B',
          status: 'Personnel Approved',
          work: 14,
          holiday: 7,
          vacation: 0,
          personal: 0,
          sickEmp: 0,
          sickFam: 0,
          misc: 0,
          total: 21,
        },
      ],
    };
  
    return data;
  };
  