<!DOCTYPE html>
<html lang="en">
<head>
    <title>
        Approved Travel Application for ${view.travelerFullName} on
        ${view.datesOfTravel}
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
    Hi ${recipient.fullName},
</p>
<p>
    The Travel Application for ${view.travelerFullName} on ${view.datesOfTravel}
    has been <strong>approved</strong>.
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
