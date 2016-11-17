<!DOCTYPE html>
<html lang="en">
<#setting date_format="MM/dd/yy">
<head>
<title>DISAPPROVED TIMESHEET ${timeRecord.beginDate} - ${timeRecord.endDate}</title>
</head>
<body>
<p>Your timesheet for the pay period
<strong>${timeRecord.beginDate} - ${timeRecord.endDate}</strong>
has been <strong>DISAPPROVED</strong> by ${rejector.getFullName()}.
</p>
<br/><br/>
<p>The reason that was given for this disapproval is:</p>
<br/><br/>
<strong>"${timeRecord.getRemarks()}"</strong>
<br/><br/>
<p>Please correct your timesheet and re-submit it to your supervisor.</p>
<br/><br/>
<p>Thank you.</p>
</body>
</html>
