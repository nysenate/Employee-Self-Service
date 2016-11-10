<!DOCTYPE html>
<html lang="en">
<#setting date_format="MM/dd/yy">
<head>
  <title>DISAPPROVED TIMESHEET ${timeRecord.beginDate} - ${timeRecord.endDate}</title>
</head>
<body>
<table>
  <tr>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      Your timesheet for date range of
      <strong>
        ${timeRecord.beginDate} - ${timeRecord.endDate}
      </strong>
      has been
      <strong>
        DISAPPROVED
      </strong>
      by ${rejector.getFullName()}
      <br>
    </td>
  </tr>
  <tr>
    <td>
      His remarks are as follows:
    </td>
  </tr>
  <tr>
    <td>
      <strong>
        "${timeRecord.getRemarks()}"
      </strong>
    </td>
  </tr>
  <tr>
    <td>
      <br>
      Please fix the timesheet and submit again.
    </td>
  </tr>
</table>
</body>
</html>
