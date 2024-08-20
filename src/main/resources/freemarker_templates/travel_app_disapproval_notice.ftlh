<!DOCTYPE html>
<html lang="en">
<head>
    <title>
        Disapproved Travel Application for ${view.travelerFullName} on ${view.datesOfTravel}
    </title>
</head>
<body>
<p>
    <i>
        *** This is an automated message. Please do not reply to this email. ***
    </i>
</p>

<br/>
<p>
    Hi ${recipient.firstName},
</p>
<p>
    The Travel Application for ${view.travelerFullName} on ${view.datesOfTravel}
    has been <strong>disapproved</strong> by ${view.disapproverFullName}.
</p>

<p>
    The reason given for disapproval is:
</p>
<p>
    <strong>${view.disapprovalReason}</strong>
</p>
<p>
    If possible, you may make changes to your application and re-submit it.
</p>
<p>
    Click <a href="${domainUrl}/travel/application/${view.appId}">here</a> to view this Travel Application.
</p>
<br/>
<p>
    Thank you.
</p>
</body>
</html>
