<!DOCTYPE html>
<html lang="en">
<head>
<title>Time Records Must Be Submitted</title>
</head>
<body>
<p><i>
*** This is an automated message.  Please do not reply to this email. ***
</i></p>
<br/>
<p>
Hi ${employee.firstName},
</p>
<p>
Please be advised that the following Time and Attendance record(s)
need to be completed and forwarded to the Senate Personnel Office:
</p>
<br/>
<#setting date_format="MM/dd/yy">
<#list timeRecords as timeRecord>
<p>
&nbsp;&nbsp;&nbsp;&nbsp;
${timeRecord.beginDate} - ${timeRecord.endDate}
&nbsp;&nbsp; - &nbsp;&nbsp; ${timeRecord.recordStatus.getName()}
</p>
</#list>
<br/>
<p>
Any questions regarding this email should be directed to the
Senate Personnel Office, Time and Attendance Unit at (518) 455-3376.
</p>
</body>
</html>
