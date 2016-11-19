<!DOCTYPE html>
<html lang="en">
<#setting date_format="MM/dd/yy">
<head>
<title>DISAPPROVED TIMESHEET ${timeRecord.beginDate} - ${timeRecord.endDate}</title>
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
Your timesheet for the pay period
<strong>${timeRecord.beginDate} - ${timeRecord.endDate}</strong>
has been <strong>DISAPPROVED</strong> by ${rejector.getFullName()}.
</p>
<p>The reason that was given for this disapproval is:</p>
<p>
<strong>"${timeRecord.getRemarks()}"</strong>
</p>
<br/>
<p>Please correct your timesheet and re-submit it to your supervisor.</p>
<br/>
<p>Thank you.</p>
</body>
</html>
