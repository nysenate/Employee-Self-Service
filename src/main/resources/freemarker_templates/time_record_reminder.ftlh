<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Time Records Must Be Submitted</title>
  </head>
  <body>
    <table>
      <tr>
        <td>
          Please be advised that the following Time and Attendance record(s)
          need to be completed and forwarded to the Senate Personnel Office:
          <br><br>
        </td>
      </tr>
      <#setting date_format="MM/dd/yy">
      <#list timeRecords as timeRecord>
        <tr>
          <td>
            &nbsp;&nbsp;&nbsp;&nbsp;
            ${timeRecord.beginDate} - ${timeRecord.endDate}
            &nbsp;&nbsp;
            ${timeRecord.recordStatus.getName()}
          </td>
        </tr>
      </#list>
      <tr>
        <td>
          <br>
          Any questions regarding this email should be directed to the
          Senate Personnel Office, Time and Attendance Unit at (518) 455-3376.
        </td>
      </tr>
    </table>
  </body>
</html>
